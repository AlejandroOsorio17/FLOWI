package com.example.demo1.infrastructure.adapter.out.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventarioJpaRepository extends JpaRepository<InventarioJpaEntity, Long> {

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