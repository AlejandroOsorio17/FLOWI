package com.example.demo1.infrastructure.seed;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo1.infrastructure.adapter.out.persistence.DetalleVentaJpaEntity;
import com.example.demo1.infrastructure.adapter.out.persistence.ProductJpaEntity;
import com.example.demo1.infrastructure.adapter.out.persistence.ProductJpaRepository;
import com.example.demo1.infrastructure.adapter.out.persistence.StoreJpaEntity;
import com.example.demo1.infrastructure.adapter.out.persistence.StoreJpaRepository;
import com.example.demo1.infrastructure.adapter.out.persistence.VentaJpaEntity;
import com.example.demo1.infrastructure.adapter.out.persistence.VentaJpaRepository;

/**
 * Siembra ventas históricas de los últimos 30 días para cada tienda.
 *
 * - Idempotente: solo siembra si la tabla "ventas" está vacía.
 * - No descuenta inventario (son datos históricos de ejemplo).
 * - Demanda diaria por producto (unidades/día/tienda) distinta según el código del producto;
 *   CAM-001 es de alta rotación (8-12 u/día) para provocar déficit frente al inventario de tienda.
 * - Se puede desactivar con app.seed.historical-sales.enabled=false.
 */
@Component
@ConditionalOnProperty(name = "app.seed.historical-sales.enabled", havingValue = "true", matchIfMissing = true)
public class HistoricalSalesSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(HistoricalSalesSeeder.class);

    private static final int DAYS = 30;
    private static final String ESTADO = "COMPLETADA";

    /** Rango de unidades diarias por tienda {min, max} según código de producto. */
    private static final Map<String, int[]> DAILY_DEMAND = new HashMap<>();
    /** Demanda por defecto para productos sin perfil definido. */
    private static final int[] DEFAULT_DEMAND = {1, 4};

    static {
        DAILY_DEMAND.put("CAM-001", new int[] {8, 12}); // alta rotación
        DAILY_DEMAND.put("JEA-001", new int[] {3, 5});  // media-alta
        DAILY_DEMAND.put("SUD-001", new int[] {2, 4});  // media
        DAILY_DEMAND.put("VES-001", new int[] {1, 2});  // baja
        DAILY_DEMAND.put("CHA-001", new int[] {0, 1});  // muy baja
    }

    private final VentaJpaRepository ventaRepository;
    private final StoreJpaRepository storeRepository;
    private final ProductJpaRepository productRepository;

    public HistoricalSalesSeeder(VentaJpaRepository ventaRepository,
                                 StoreJpaRepository storeRepository,
                                 ProductJpaRepository productRepository) {
        this.ventaRepository = ventaRepository;
        this.storeRepository = storeRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (ventaRepository.count() > 0) {
            log.info("HistoricalSalesSeeder: la tabla ventas ya tiene datos, no se siembra nada.");
            return;
        }

        List<StoreJpaEntity> stores = storeRepository.findAll();
        List<ProductJpaEntity> products = productRepository.findAll();
        if (stores.isEmpty() || products.isEmpty()) {
            log.warn("HistoricalSalesSeeder: no hay tiendas o productos; se omite la siembra de ventas.");
            return;
        }

        // Semilla fija: la demanda es reproducible entre ejecuciones sobre una base vacía.
        Random random = new Random(42);
        LocalDateTime now = LocalDateTime.now();

        int ventasCreadas = 0;
        int detallesCreados = 0;

        for (StoreJpaEntity store : stores) {
            List<VentaJpaEntity> ventas = new ArrayList<>();

            for (int daysAgo = 0; daysAgo < DAYS; daysAgo++) {
                // 2-4 tickets por día y tienda; las unidades del día se reparten entre ellos.
                int tickets = 2 + random.nextInt(3);
                List<Map<ProductJpaEntity, Integer>> ticketItems = new ArrayList<>();
                for (int t = 0; t < tickets; t++) {
                    ticketItems.add(new LinkedHashMap<>());
                }

                for (ProductJpaEntity product : products) {
                    int[] range = DAILY_DEMAND.getOrDefault(product.getCode(), DEFAULT_DEMAND);
                    int units = range[0] + random.nextInt(range[1] - range[0] + 1);
                    for (int u = 0; u < units; u++) {
                        ticketItems.get(random.nextInt(tickets)).merge(product, 1, Integer::sum);
                    }
                }

                for (Map<ProductJpaEntity, Integer> items : ticketItems) {
                    if (items.isEmpty()) {
                        continue;
                    }
                    // Fecha relativa a now(): nunca en el futuro.
                    LocalDateTime fecha = now.minusDays(daysAgo)
                            .minusHours(random.nextInt(12))
                            .minusMinutes(random.nextInt(60));

                    VentaJpaEntity venta = new VentaJpaEntity();
                    venta.setStoreId(store.getId());
                    venta.setFecha(fecha);
                    venta.setEstado(ESTADO);

                    List<DetalleVentaJpaEntity> detalles = new ArrayList<>();
                    BigDecimal total = BigDecimal.ZERO;
                    for (Map.Entry<ProductJpaEntity, Integer> entry : items.entrySet()) {
                        ProductJpaEntity product = entry.getKey();
                        int cantidad = entry.getValue();
                        BigDecimal precio = BigDecimal.valueOf(product.getPrice() == null ? 0.0 : product.getPrice())
                                .setScale(2, RoundingMode.HALF_UP);

                        DetalleVentaJpaEntity detalle = new DetalleVentaJpaEntity();
                        detalle.setVenta(venta);
                        detalle.setProductId(product.getId());
                        detalle.setCantidad(cantidad);
                        detalle.setPrecioUnitario(precio);
                        detalles.add(detalle);

                        total = total.add(precio.multiply(BigDecimal.valueOf(cantidad)));
                    }

                    venta.setDetalles(detalles);
                    venta.setTotal(total.setScale(2, RoundingMode.HALF_UP));
                    ventas.add(venta);
                    detallesCreados += detalles.size();
                }
            }

            // El cascade ALL de Venta -> detalles persiste también las líneas de detalle.
            ventaRepository.saveAll(ventas);
            ventasCreadas += ventas.size();
        }

        log.info("HistoricalSalesSeeder: sembradas {} ventas y {} detalles ({} tiendas, {} días).",
                ventasCreadas, detallesCreados, stores.size(), DAYS);
    }
}
