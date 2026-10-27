package com.example.demo1.infrastructure.adapter.out.persistence;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventarioJpaRepository extends JpaRepository<InventarioJpaEntity, Long> {

    @Query("SELECT new com.example.demo1.domain.InventoryItem(i.productId, p.name, i.cantidad) " +
           "FROM InventarioJpaEntity i, ProductJpaEntity p " +
           "WHERE i.productId = p.id AND i.storeId = :storeId")
    List<com.example.demo1.domain.InventoryItem> findInventoryByStore(
            @Param("storeId") Long storeId);

    @Query("SELECT new com.example.demo1.infrastructure.adapter.in.web.dto.EnrichedInventoryItemResponse(" +
           "p.id, p.name, p.code, 'N/A', p.description, p.price, i.cantidad, 10) " +
           "FROM InventarioJpaEntity i, ProductJpaEntity p " +
           "WHERE i.productId = p.id AND i.storeId = :storeId")
    List<com.example.demo1.infrastructure.adapter.in.web.dto.EnrichedInventoryItemResponse> findEnrichedInventoryByStore(
            @Param("storeId") Long storeId);

    @Query("SELECT new com.example.demo1.domain.InventoryItem(i.productId, p.name, i.cantidad) " +
           "FROM InventarioJpaEntity i, ProductJpaEntity p " +
           "WHERE i.productId = p.id AND i.storeId = :storeId AND i.cantidad <= :threshold")
    List<com.example.demo1.domain.InventoryItem> findLowStockByStore(
            @Param("storeId") Long storeId, @Param("threshold") int threshold);

    @Query("SELECT i.cantidad FROM InventarioJpaEntity i " +
           "WHERE i.storeId = :storeId AND i.productId = :productId")
    Optional<Integer> findCantidad(@Param("storeId") Long storeId,
                                   @Param("productId") Long productId);

    @Modifying
    @Query("UPDATE InventarioJpaEntity i SET i.cantidad = i.cantidad - :qty " +
           "WHERE i.storeId = :storeId AND i.productId = :productId")
    void decrementCantidad(@Param("storeId") Long storeId,
                           @Param("productId") Long productId,
                           @Param("qty") int qty);
}
