package com.example.demo1.application.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo1.domain.Store;
import com.example.demo1.domain.exception.ResourceNotFoundException;
import com.example.demo1.domain.port.out.ProductRepository;
import com.example.demo1.domain.port.out.ReplenishmentQueryRepository;
import com.example.demo1.domain.port.out.StoreRepository;
import com.example.demo1.domain.replenishment.CenterStockLevel;
import com.example.demo1.domain.replenishment.OriginCenterSelector;
import com.example.demo1.domain.replenishment.ProductDemand;
import com.example.demo1.domain.replenishment.ReplenishmentCalculator;
import com.example.demo1.domain.replenishment.ReplenishmentParams;
import com.example.demo1.domain.replenishment.ReplenishmentResult;
import com.example.demo1.domain.replenishment.StoreStockLevel;
import com.example.demo1.infrastructure.adapter.in.web.dto.RecommendedQuantityResponse;
import com.example.demo1.infrastructure.adapter.in.web.dto.ReplenishmentRecommendationResponse;
import com.example.demo1.infrastructure.adapter.in.web.dto.RequiredProductResponse;

@Service
@Transactional(readOnly = true)
public class ReplenishmentService {

    /** Resultado calculado para un producto de la tienda, con su centro origen (puede ser null). */
    private record Entry(StoreStockLevel level, ReplenishmentResult result, CenterStockLevel origin) {
    }

    /** Prioridad (ALTA, MEDIA, BAJA), luego déficit descendente, luego id de producto. */
    private static final Comparator<Entry> ENTRY_ORDER =
            Comparator.comparing((Entry e) -> e.result().priority())
                    .thenComparing((Entry e) -> e.result().deficit(), Comparator.reverseOrder())
                    .thenComparing((Entry e) -> e.level().productId());

    private final ReplenishmentQueryRepository queryRepository;
    private final ReplenishmentCalculator calculator;
    private final ReplenishmentParams params;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final Clock clock;

    public ReplenishmentService(ReplenishmentQueryRepository queryRepository,
                                ReplenishmentCalculator calculator,
                                ReplenishmentParams params,
                                StoreRepository storeRepository,
                                ProductRepository productRepository,
                                Clock clock) {
        this.queryRepository = queryRepository;
        this.calculator = calculator;
        this.params = params;
        this.storeRepository = storeRepository;
        this.productRepository = productRepository;
        this.clock = clock;
    }

    /** Productos de la tienda que requieren reposición, ordenados por prioridad y déficit descendente. */
    public List<RequiredProductResponse> findRequiredProducts(Long storeId) {
        Store store = requireStore(storeId);
        Map<Long, Long> demand = demandByProduct(storeId);

        return queryRepository.findStoreStockLevels(storeId).stream()
                .map(level -> evaluate(level, demand.getOrDefault(level.productId(), 0L),
                        store.getCity(), List.of(), false))
                .filter(e -> e.result().requiresReplenishment())
                .sorted(ENTRY_ORDER)
                .map(e -> new RequiredProductResponse(
                        e.level().productId(), e.level().productCode(), e.level().productName(),
                        e.level().currentStock(), e.level().minimumStock(),
                        e.result().dailyDemand(), e.result().deficit(), e.result().priority()))
                .toList();
    }

    /** Cantidad recomendada de un producto en la tienda; incluye el caso sin déficit (cantidad 0). */
    public RecommendedQuantityResponse findRecommendedQuantity(Long storeId, Long productId) {
        Store store = requireStore(storeId);
        productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + productId));

        StoreStockLevel level = queryRepository.findStoreStockLevel(storeId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + productId));
        long unitsSold = queryRepository.findUnitsSold(storeId, productId, since());
        List<CenterStockLevel> centers = queryRepository.findCenterStockLevelsByProduct(productId);

        Entry e = evaluate(level, unitsSold, store.getCity(), centers, true);
        CenterStockLevel origin = e.origin();
        return new RecommendedQuantityResponse(
                storeId, level.productId(), level.productCode(), level.productName(),
                level.currentStock(), level.minimumStock(),
                e.result().dailyDemand(), e.result().deficit(), e.result().recommendedQuantity(),
                origin == null ? null : origin.centerId(),
                origin == null ? null : origin.centerName(),
                origin == null ? null : origin.stock(),
                e.result().priority());
    }

    /** Recomendaciones de reposición (productos con déficit) con su centro origen. */
    public List<ReplenishmentRecommendationResponse> findRecommendations(Long storeId) {
        Store store = requireStore(storeId);
        Map<Long, Long> demand = demandByProduct(storeId);
        Map<Long, List<CenterStockLevel>> centersByProduct = queryRepository.findCenterStockLevels().stream()
                .collect(Collectors.groupingBy(CenterStockLevel::productId));

        return queryRepository.findStoreStockLevels(storeId).stream()
                .map(level -> evaluate(level, demand.getOrDefault(level.productId(), 0L),
                        store.getCity(), centersByProduct.getOrDefault(level.productId(), List.of()), true))
                .filter(e -> e.result().requiresReplenishment())
                .sorted(ENTRY_ORDER)
                .map(e -> new ReplenishmentRecommendationResponse(
                        storeId, store.getName(),
                        e.level().productId(), e.level().productCode(), e.level().productName(),
                        e.origin() == null ? null : e.origin().centerId(),
                        e.origin() == null ? null : e.origin().centerName(),
                        e.level().currentStock(), e.result().deficit(), e.result().recommendedQuantity(),
                        e.result().priority(), e.result().dailyDemand()))
                .toList();
    }

    private Store requireStore(Long storeId) {
        return storeRepository.findAll().stream()
                .filter(s -> storeId.equals(s.getId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Tienda no encontrada: " + storeId));
    }

    private LocalDateTime since() {
        return LocalDateTime.now(clock).minusDays(params.demandWindowDays());
    }

    private Map<Long, Long> demandByProduct(Long storeId) {
        return queryRepository.findDemandByStore(storeId, since()).stream()
                .collect(Collectors.toMap(ProductDemand::productId, ProductDemand::unitsSold, Long::sum));
    }

    private Entry evaluate(StoreStockLevel level, long unitsSold, String storeCity,
                           List<CenterStockLevel> centers, boolean withOrigin) {
        CenterStockLevel origin = withOrigin
                ? OriginCenterSelector.select(storeCity, centers).orElse(null)
                : null;
        // Sin centro con stock: centerStock 0 => cantidad recomendada 0, el producto sigue listado.
        Integer centerStock = withOrigin ? (origin == null ? 0 : origin.stock()) : null;
        int sold = (int) Math.min(unitsSold, Integer.MAX_VALUE);
        ReplenishmentResult result = calculator.calculate(
                level.currentStock(), level.minimumStock(), sold, centerStock);
        return new Entry(level, result, origin);
    }
}
