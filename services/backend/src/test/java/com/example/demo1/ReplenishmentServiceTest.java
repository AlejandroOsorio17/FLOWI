package com.example.demo1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo1.application.service.ReplenishmentService;
import com.example.demo1.domain.Product;
import com.example.demo1.domain.Store;
import com.example.demo1.domain.exception.ResourceNotFoundException;
import com.example.demo1.domain.port.out.ProductRepository;
import com.example.demo1.domain.port.out.ReplenishmentQueryRepository;
import com.example.demo1.domain.port.out.StoreRepository;
import com.example.demo1.domain.replenishment.CenterStockLevel;
import com.example.demo1.domain.replenishment.ProductDemand;
import com.example.demo1.domain.replenishment.ReplenishmentCalculator;
import com.example.demo1.domain.replenishment.ReplenishmentParams;
import com.example.demo1.domain.replenishment.ReplenishmentPriority;
import com.example.demo1.domain.replenishment.StoreStockLevel;
import com.example.demo1.infrastructure.adapter.in.web.dto.RecommendedQuantityResponse;
import com.example.demo1.infrastructure.adapter.in.web.dto.ReplenishmentRecommendationResponse;
import com.example.demo1.infrastructure.adapter.in.web.dto.RequiredProductResponse;

@ExtendWith(MockitoExtension.class)
class ReplenishmentServiceTest {

    private static final Long STORE_ID = 1L;
    // Reloj fijo: now = 2026-03-31T10:00 => since (ventana 30 dias) = 2026-03-01T10:00
    private static final LocalDateTime SINCE = LocalDateTime.of(2026, 3, 1, 10, 0);

    @Mock
    private ReplenishmentQueryRepository queryRepository;
    @Mock
    private StoreRepository storeRepository;
    @Mock
    private ProductRepository productRepository;

    private ReplenishmentService service;

    @BeforeEach
    void setUp() {
        ReplenishmentParams params = new ReplenishmentParams(30, 7, 3, 7);
        Clock clock = Clock.fixed(Instant.parse("2026-03-31T10:00:00Z"), ZoneOffset.UTC);
        service = new ReplenishmentService(queryRepository, new ReplenishmentCalculator(params),
                params, storeRepository, productRepository, clock);
    }

    // ---------- helpers ----------

    private void givenStore(String city) {
        when(storeRepository.findAll())
                .thenReturn(List.of(new Store(STORE_ID, "S-1", "Zara Andino", "addr", city)));
    }

    private static StoreStockLevel level(long id, int stock, int minimum) {
        return new StoreStockLevel(id, "Product " + id, "P-" + id, stock, minimum);
    }

    private static CenterStockLevel center(long id, String city, long productId, int stock) {
        return new CenterStockLevel(id, "Center " + id, city, productId, stock);
    }

    /** Escenario de una recomendacion: producto 1 con stock 20, minimo 10 y 300 vendidas (deficit 50). */
    private void givenSingleDeficitProduct(List<CenterStockLevel> centers) {
        when(queryRepository.findDemandByStore(STORE_ID, SINCE)).thenReturn(List.of(new ProductDemand(1L, 300L)));
        when(queryRepository.findStoreStockLevels(STORE_ID)).thenReturn(List.of(level(1, 20, 10)));
        when(queryRepository.findCenterStockLevels()).thenReturn(centers);
    }

    @Test
    void shouldIncludeProductWithDeficitAndExcludeOneWithoutDeficit() {
        // Arrange: p1 stock 20, min 10, 300 vendidas => target 70, deficit 50; p2 stock 100, sin ventas => sin deficit
        givenStore("Bogota");
        when(queryRepository.findDemandByStore(STORE_ID, SINCE)).thenReturn(List.of(new ProductDemand(1L, 300L)));
        when(queryRepository.findStoreStockLevels(STORE_ID)).thenReturn(List.of(level(1, 20, 10), level(2, 100, 10)));

        // Act
        List<RequiredProductResponse> result = service.findRequiredProducts(STORE_ID);

        // Assert
        assertEquals(1, result.size());
        RequiredProductResponse item = result.get(0);
        assertEquals(1L, item.productId());
        assertEquals(50, item.deficit());
        assertEquals(10.0, item.dailyDemand(), 1e-9);
        assertEquals(ReplenishmentPriority.ALTA, item.priority());
    }

    @Test
    void shouldOrderByPriorityThenDeficitDescending() {
        // Arrange: p1 MEDIA def 1; p2 ALTA def 50; p3 ALTA def 10 (stock 0); p4 MEDIA def 30; p5 BAJA def 5
        givenStore("Bogota");
        when(queryRepository.findDemandByStore(STORE_ID, SINCE)).thenReturn(List.of(
                new ProductDemand(1L, 300L), new ProductDemand(2L, 300L), new ProductDemand(4L, 300L)));
        when(queryRepository.findStoreStockLevels(STORE_ID)).thenReturn(List.of(
                level(1, 69, 10), level(2, 20, 10), level(3, 0, 10), level(4, 40, 10), level(5, 5, 10)));

        // Act
        List<RequiredProductResponse> result = service.findRequiredProducts(STORE_ID);

        // Assert: ALTA (50, 10), MEDIA (30, 1), BAJA (5)
        assertEquals(List.of(2L, 3L, 4L, 1L, 5L), result.stream().map(RequiredProductResponse::productId).toList());
        assertEquals(List.of(ReplenishmentPriority.ALTA, ReplenishmentPriority.ALTA, ReplenishmentPriority.MEDIA,
                ReplenishmentPriority.MEDIA, ReplenishmentPriority.BAJA),
                result.stream().map(RequiredProductResponse::priority).toList());
    }

    @Test
    void shouldComputeSinceUsingThirtyDayWindowAndReturnEmptyListWhenNothingRequired() {
        // Arrange: reloj fijo 2026-03-31T10:00 => since = 2026-03-01T10:00
        givenStore("Bogota");
        when(queryRepository.findDemandByStore(STORE_ID, SINCE)).thenReturn(List.of());
        when(queryRepository.findStoreStockLevels(STORE_ID)).thenReturn(List.of());

        // Act
        List<RequiredProductResponse> result = service.findRequiredProducts(STORE_ID);

        // Assert
        assertEquals(List.of(), result);
        verify(queryRepository).findDemandByStore(STORE_ID, SINCE);
    }

    @Test
    void shouldThrowNotFoundWhenStoreDoesNotExist() {
        // Arrange
        when(storeRepository.findAll()).thenReturn(List.of());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> service.findRequiredProducts(STORE_ID));
        assertThrows(ResourceNotFoundException.class, () -> service.findRecommendedQuantity(STORE_ID, 1L));
        assertThrows(ResourceNotFoundException.class, () -> service.findRecommendations(STORE_ID));
    }

    @Test
    void shouldThrowNotFoundWhenProductDoesNotExist() {
        // Arrange
        givenStore("Bogota");
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> service.findRecommendedQuantity(STORE_ID, 99L));
    }

    @Test
    void shouldPreferCenterInSameCityEvenWhenAnotherHasMoreStock() {
        // Arrange: ciudad de la tienda con tilde; centro 2 en "bogota " (sin tilde, con espacio) y menos stock
        givenStore("Bogotá");
        givenSingleDeficitProduct(List.of(center(1, "Medellin", 1, 200), center(2, "bogota ", 1, 60)));

        // Act
        List<ReplenishmentRecommendationResponse> result = service.findRecommendations(STORE_ID);

        // Assert
        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).originCenterId());
        assertEquals(50, result.get(0).recommendedQuantity());
    }

    @Test
    void shouldFallbackToCenterWithHighestStockWhenNoneInSameCity() {
        // Arrange
        givenStore("Cali");
        givenSingleDeficitProduct(List.of(center(1, "Bogota", 1, 80), center(2, "Medellin", 1, 120)));

        // Act
        List<ReplenishmentRecommendationResponse> result = service.findRecommendations(STORE_ID);

        // Assert
        assertEquals(2L, result.get(0).originCenterId());
        assertEquals("Center 2", result.get(0).originCenterName());
    }

    @Test
    void shouldBreakTiesByLowestCenterId() {
        // Arrange: dos centros con el mismo stock, el de id mayor aparece primero
        givenStore("Cali");
        givenSingleDeficitProduct(List.of(center(5, "Bogota", 1, 100), center(3, "Medellin", 1, 100)));

        // Act
        List<ReplenishmentRecommendationResponse> result = service.findRecommendations(STORE_ID);

        // Assert
        assertEquals(3L, result.get(0).originCenterId());
    }

    @Test
    void shouldKeepProductListedWithNullOriginAndZeroQuantityWhenNoCenterHasStock() {
        // Arrange: ningun centro con stock
        givenStore("Bogota");
        givenSingleDeficitProduct(List.of(center(1, "Bogota", 1, 0), center(2, "Medellin", 1, 0)));

        // Act
        List<ReplenishmentRecommendationResponse> result = service.findRecommendations(STORE_ID);

        // Assert
        assertEquals(1, result.size());
        assertNull(result.get(0).originCenterId());
        assertNull(result.get(0).originCenterName());
        assertEquals(50, result.get(0).deficit());
        assertEquals(0, result.get(0).recommendedQuantity());
    }

    @Test
    void shouldKeepProductListedWhenThereAreNoCenters() {
        // Arrange
        givenStore("Bogota");
        givenSingleDeficitProduct(List.of());

        // Act
        List<ReplenishmentRecommendationResponse> result = service.findRecommendations(STORE_ID);

        // Assert
        assertEquals(1, result.size());
        assertNull(result.get(0).originCenterId());
        assertEquals(0, result.get(0).recommendedQuantity());
    }

    @Test
    void shouldLimitRecommendedQuantityToOriginCenterStock() {
        // Arrange: deficit 50, centro con 30
        givenStore("Bogota");
        givenSingleDeficitProduct(List.of(center(1, "Bogota", 1, 30)));

        // Act
        List<ReplenishmentRecommendationResponse> result = service.findRecommendations(STORE_ID);

        // Assert
        assertEquals(50, result.get(0).deficit());
        assertEquals(30, result.get(0).recommendedQuantity());
        assertEquals("Zara Andino", result.get(0).storeName());
    }

    @Test
    void shouldReturnZeroQuantityForRecommendedQuantityWhenProductHasNoDeficit() {
        // Arrange: stock 100, sin ventas => target 10 => sin deficit
        givenStore("Bogota");
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(new Product(1L, "P-1", "Product 1", "desc", 10.0)));
        when(queryRepository.findStoreStockLevel(STORE_ID, 1L)).thenReturn(Optional.of(level(1, 100, 10)));
        when(queryRepository.findUnitsSold(STORE_ID, 1L, SINCE)).thenReturn(0L);
        when(queryRepository.findCenterStockLevelsByProduct(1L)).thenReturn(List.of(center(1, "Bogota", 1, 80)));

        // Act
        RecommendedQuantityResponse result = service.findRecommendedQuantity(STORE_ID, 1L);

        // Assert
        assertEquals(0, result.deficit());
        assertEquals(0, result.recommendedQuantity());
        assertNull(result.priority());
    }

    @Test
    void shouldReturnRecommendedQuantityWithOriginWhenProductHasDeficit() {
        // Arrange: 300 vendidas, stock 20, min 10 => deficit 50; centro con 30
        givenStore("Bogota");
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(new Product(1L, "P-1", "Product 1", "desc", 10.0)));
        when(queryRepository.findStoreStockLevel(STORE_ID, 1L)).thenReturn(Optional.of(level(1, 20, 10)));
        when(queryRepository.findUnitsSold(STORE_ID, 1L, SINCE)).thenReturn(300L);
        when(queryRepository.findCenterStockLevelsByProduct(1L)).thenReturn(List.of(center(7, "Bogota", 1, 30)));

        // Act
        RecommendedQuantityResponse result = service.findRecommendedQuantity(STORE_ID, 1L);

        // Assert
        assertEquals(50, result.deficit());
        assertEquals(30, result.recommendedQuantity());
        assertEquals(7L, result.originCenterId());
        assertEquals(30, result.originCenterStock());
        assertEquals(ReplenishmentPriority.ALTA, result.priority());
    }
}
