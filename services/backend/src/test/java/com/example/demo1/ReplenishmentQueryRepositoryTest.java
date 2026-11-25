package com.example.demo1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.example.demo1.domain.replenishment.CenterStockLevel;
import com.example.demo1.domain.replenishment.ProductDemand;
import com.example.demo1.domain.replenishment.StoreStockLevel;
import com.example.demo1.infrastructure.adapter.out.persistence.DetalleVentaJpaEntity;
import com.example.demo1.infrastructure.adapter.out.persistence.DistributionCenterJpaEntity;
import com.example.demo1.infrastructure.adapter.out.persistence.InventarioCentroJpaEntity;
import com.example.demo1.infrastructure.adapter.out.persistence.InventarioJpaEntity;
import com.example.demo1.infrastructure.adapter.out.persistence.ProductJpaEntity;
import com.example.demo1.infrastructure.adapter.out.persistence.ReplenishmentQueryRepositoryImpl;
import com.example.demo1.infrastructure.adapter.out.persistence.VentaJpaEntity;

import jakarta.persistence.EntityManager;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.sql.init.mode=never"
})
@Import(ReplenishmentQueryRepositoryImpl.class)
class ReplenishmentQueryRepositoryTest {

    private static final long STORE_1 = 1L;
    private static final long STORE_2 = 2L;
    private static final LocalDateTime SINCE = LocalDateTime.of(2026, 3, 1, 0, 0);

    @Autowired
    private EntityManager em;

    @Autowired
    private ReplenishmentQueryRepositoryImpl repository;

    private Long p1;
    private Long p2;
    private Long p3;
    private Long c1;
    private Long c2;

    @BeforeEach
    void setUp() {
        // p1 minimum 20; p2 minimum null (=> 10); p3 minimum 5 y sin fila de inventario en tiendas
        p1 = persistProduct("P-1", "Product One", 20);
        p2 = persistProduct("P-2", "Product Two", null);
        p3 = persistProduct("P-3", "Product Three", 5);

        persistStoreStock(STORE_1, p1, 15);
        persistStoreStock(STORE_1, p2, 40);
        persistStoreStock(STORE_2, p1, 99);

        // c1 tiene p1 y p2; c2 solo p1; ninguno tiene p3
        c1 = persistCenter("DC-1", "Center One", "Bogota");
        c2 = persistCenter("DC-2", "Center Two", "Medellin");
        persistCenterStock(c1, p1, 100);
        persistCenterStock(c1, p2, 50);
        persistCenterStock(c2, p1, 30);

        // Tienda 1, COMPLETADA dentro de la ventana: p1 = 5 + 7, p2 = 3
        persistSale(STORE_1, LocalDateTime.of(2026, 3, 10, 10, 0), "COMPLETADA", p1, 5);
        persistSale(STORE_1, LocalDateTime.of(2026, 3, 10, 11, 0), "COMPLETADA", p2, 3);
        persistSale(STORE_1, LocalDateTime.of(2026, 3, 15, 9, 0), "COMPLETADA", p1, 7);
        // Justo en el limite de la ventana (fecha >= since): cuenta
        persistSale(STORE_1, SINCE, "COMPLETADA", p2, 2);
        // Excluidas: fuera de ventana, estado distinto de COMPLETADA
        persistSale(STORE_1, LocalDateTime.of(2026, 2, 20, 10, 0), "COMPLETADA", p1, 100);
        persistSale(STORE_1, LocalDateTime.of(2026, 3, 12, 10, 0), "CANCELADA", p1, 50);
        // Otra tienda
        persistSale(STORE_2, LocalDateTime.of(2026, 3, 12, 10, 0), "COMPLETADA", p1, 1000);

        em.flush();
        em.clear();
    }

    @Test
    void shouldSumDemandByProductExcludingOutOfWindowNonCompletedAndOtherStore() {
        // Arrange: datos de setUp (tienda 1: p1 = 12, p2 = 5; p3 sin ventas)
        // Act
        List<ProductDemand> demand = repository.findDemandByStore(STORE_1, SINCE);

        // Assert
        assertEquals(List.of(new ProductDemand(p1, 12L), new ProductDemand(p2, 5L)), demand);
    }

    @Test
    void shouldReturnUnitsSoldPerProductAndZeroWhenNoSales() {
        // Arrange: p3 no tiene ventas; la tienda 2 vendio 1000 de p1
        // Act & Assert
        assertEquals(12L, repository.findUnitsSold(STORE_1, p1, SINCE));
        assertEquals(1000L, repository.findUnitsSold(STORE_2, p1, SINCE));
        assertEquals(0L, repository.findUnitsSold(STORE_1, p3, SINCE));
        assertEquals(0L, repository.findUnitsSold(STORE_1, p1, LocalDateTime.of(2027, 1, 1, 0, 0)));
    }

    @Test
    void shouldReturnEmptyDemandWhenStoreHasNoSales() {
        // Arrange: tienda sin ventas
        // Act
        List<ProductDemand> demand = repository.findDemandByStore(999L, SINCE);

        // Assert
        assertTrue(demand.isEmpty());
    }

    // ---------- helpers ----------

    private Long persistProduct(String code, String name, Integer minimum) {
        ProductJpaEntity product = new ProductJpaEntity(null, code, name, "desc", null, null, minimum, 10.0);
        em.persist(product);
        return product.getId();
    }

    private void persistStoreStock(long storeId, Long productId, int quantity) {
        InventarioJpaEntity entity = new InventarioJpaEntity();
        entity.setStoreId(storeId);
        entity.setProductId(productId);
        entity.setCantidad(quantity);
        em.persist(entity);
    }

    private Long persistCenter(String code, String name, String city) {
        DistributionCenterJpaEntity center = new DistributionCenterJpaEntity(null, code, name, "addr", city);
        em.persist(center);
        return center.getId();
    }

    private void persistCenterStock(Long centerId, Long productId, int quantity) {
        InventarioCentroJpaEntity entity = new InventarioCentroJpaEntity();
        entity.setDistributionCenterId(centerId);
        entity.setProductId(productId);
        entity.setCantidad(quantity);
        em.persist(entity);
    }

    private void persistSale(long storeId, LocalDateTime fecha, String estado, Long productId, int quantity) {
        VentaJpaEntity venta = new VentaJpaEntity();
        venta.setStoreId(storeId);
        venta.setFecha(fecha);
        venta.setEstado(estado);
        venta.setTotal(BigDecimal.valueOf(quantity).multiply(BigDecimal.TEN));

        DetalleVentaJpaEntity detalle = new DetalleVentaJpaEntity();
        detalle.setVenta(venta);
        detalle.setProductId(productId);
        detalle.setCantidad(quantity);
        detalle.setPrecioUnitario(BigDecimal.TEN);

        venta.setDetalles(new java.util.ArrayList<>(List.of(detalle)));
        em.persist(venta);
    }

    @Test
    void shouldReturnStoreStockLevelsOrderedByProductWithDefaultsForMissingData() {
        // Arrange: tienda 1 tiene p1 y p2; p3 no tiene fila; p2 tiene minimum null
        // Act
        List<StoreStockLevel> levels = repository.findStoreStockLevels(STORE_1);

        // Assert
        assertEquals(List.of(
                new StoreStockLevel(p1, "Product One", "P-1", 15, 20),
                new StoreStockLevel(p2, "Product Two", "P-2", 40, 10),
                new StoreStockLevel(p3, "Product Three", "P-3", 0, 5)), levels);
    }

    @Test
    void shouldReturnZeroStockForAllProductsMissingInStoreInventory() {
        // Arrange: tienda 2 solo tiene fila de p1
        // Act
        List<StoreStockLevel> levels = repository.findStoreStockLevels(STORE_2);

        // Assert
        assertEquals(List.of(
                new StoreStockLevel(p1, "Product One", "P-1", 99, 20),
                new StoreStockLevel(p2, "Product Two", "P-2", 0, 10),
                new StoreStockLevel(p3, "Product Three", "P-3", 0, 5)), levels);
    }

    @Test
    void shouldReturnSingleStoreStockLevel() {
        // Arrange: p2 con minimum null, p3 sin inventario, y un producto inexistente
        // Act & Assert
        assertEquals(new StoreStockLevel(p2, "Product Two", "P-2", 40, 10),
                repository.findStoreStockLevel(STORE_1, p2).orElseThrow());
        assertEquals(new StoreStockLevel(p3, "Product Three", "P-3", 0, 5),
                repository.findStoreStockLevel(STORE_1, p3).orElseThrow());
        assertTrue(repository.findStoreStockLevel(STORE_1, 9999L).isEmpty());
    }

    @Test
    void shouldReturnAllCenterProductCombinationsOrderedWithZeroForMissingRows() {
        // Arrange: 2 centros x 3 productos = 6 filas; solo 3 existen en inventario_centro
        // Act
        List<CenterStockLevel> levels = repository.findCenterStockLevels();

        // Assert
        assertEquals(List.of(
                new CenterStockLevel(c1, "Center One", "Bogota", p1, 100),
                new CenterStockLevel(c1, "Center One", "Bogota", p2, 50),
                new CenterStockLevel(c1, "Center One", "Bogota", p3, 0),
                new CenterStockLevel(c2, "Center Two", "Medellin", p1, 30),
                new CenterStockLevel(c2, "Center Two", "Medellin", p2, 0),
                new CenterStockLevel(c2, "Center Two", "Medellin", p3, 0)), levels);
    }

    @Test
    void shouldReturnAllCentersForProductWithZeroWhenNoRow() {
        // Arrange: p2 solo existe en c1; p3 no existe en ningun centro
        // Act & Assert
        assertEquals(List.of(
                new CenterStockLevel(c1, "Center One", "Bogota", p2, 50),
                new CenterStockLevel(c2, "Center Two", "Medellin", p2, 0)),
                repository.findCenterStockLevelsByProduct(p2));
        assertEquals(List.of(
                new CenterStockLevel(c1, "Center One", "Bogota", p3, 0),
                new CenterStockLevel(c2, "Center Two", "Medellin", p3, 0)),
                repository.findCenterStockLevelsByProduct(p3));
        assertEquals(List.of(
                new CenterStockLevel(c1, "Center One", "Bogota", p1, 100),
                new CenterStockLevel(c2, "Center Two", "Medellin", p1, 30)),
                repository.findCenterStockLevelsByProduct(p1));
    }
}
