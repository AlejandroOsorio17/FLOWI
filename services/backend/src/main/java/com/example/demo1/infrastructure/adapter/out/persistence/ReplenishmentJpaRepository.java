package com.example.demo1.infrastructure.adapter.out.persistence;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.example.demo1.domain.replenishment.CenterStockLevel;
import com.example.demo1.domain.replenishment.ProductDemand;
import com.example.demo1.domain.replenishment.StoreStockLevel;

/**
 * Consultas de solo lectura para reposición. Extiende Repository (no JpaRepository)
 * para exponer únicamente estas consultas; InventarioJpaEntity es solo el tipo de dominio de referencia.
 */
public interface ReplenishmentJpaRepository extends Repository<InventarioJpaEntity, Long> {

    @Query("SELECT new com.example.demo1.domain.replenishment.ProductDemand(d.productId, SUM(d.cantidad)) " +
           "FROM DetalleVentaJpaEntity d JOIN d.venta v " +
           "WHERE v.storeId = :storeId AND v.estado = 'COMPLETADA' AND v.fecha >= :since " +
           "GROUP BY d.productId " +
           "ORDER BY d.productId")
    List<ProductDemand> findDemandByStore(@Param("storeId") Long storeId,
                                          @Param("since") LocalDateTime since);

    @Query("SELECT COALESCE(SUM(d.cantidad), 0L) " +
           "FROM DetalleVentaJpaEntity d JOIN d.venta v " +
           "WHERE v.storeId = :storeId AND d.productId = :productId " +
           "AND v.estado = 'COMPLETADA' AND v.fecha >= :since")
    Long findUnitsSold(@Param("storeId") Long storeId,
                       @Param("productId") Long productId,
                       @Param("since") LocalDateTime since);

    @Query("SELECT new com.example.demo1.domain.replenishment.StoreStockLevel(" +
           "p.id, p.name, p.code, COALESCE(i.cantidad, 0), COALESCE(p.minimum, 10)) " +
           "FROM ProductJpaEntity p " +
           "LEFT JOIN InventarioJpaEntity i ON i.productId = p.id AND i.storeId = :storeId " +
           "ORDER BY p.id")
    List<StoreStockLevel> findStoreStockLevels(@Param("storeId") Long storeId);

    @Query("SELECT new com.example.demo1.domain.replenishment.StoreStockLevel(" +
           "p.id, p.name, p.code, COALESCE(i.cantidad, 0), COALESCE(p.minimum, 10)) " +
           "FROM ProductJpaEntity p " +
           "LEFT JOIN InventarioJpaEntity i ON i.productId = p.id AND i.storeId = :storeId " +
           "WHERE p.id = :productId")
    Optional<StoreStockLevel> findStoreStockLevel(@Param("storeId") Long storeId,
                                                  @Param("productId") Long productId);

    @Query("SELECT new com.example.demo1.domain.replenishment.CenterStockLevel(" +
           "dc.id, dc.name, dc.city, p.id, COALESCE(ic.cantidad, 0)) " +
           "FROM DistributionCenterJpaEntity dc " +
           "CROSS JOIN ProductJpaEntity p " +
           "LEFT JOIN InventarioCentroJpaEntity ic " +
           "ON ic.distributionCenterId = dc.id AND ic.productId = p.id " +
           "ORDER BY dc.id, p.id")
    List<CenterStockLevel> findCenterStockLevels();

    @Query("SELECT new com.example.demo1.domain.replenishment.CenterStockLevel(" +
           "dc.id, dc.name, dc.city, p.id, COALESCE(ic.cantidad, 0)) " +
           "FROM DistributionCenterJpaEntity dc " +
           "CROSS JOIN ProductJpaEntity p " +
           "LEFT JOIN InventarioCentroJpaEntity ic " +
           "ON ic.distributionCenterId = dc.id AND ic.productId = p.id " +
           "WHERE p.id = :productId " +
           "ORDER BY dc.id")
    List<CenterStockLevel> findCenterStockLevelsByProduct(@Param("productId") Long productId);
}
