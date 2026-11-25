package com.example.demo1.infrastructure.adapter.out.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InventarioCentroJpaRepository extends JpaRepository<InventarioCentroJpaEntity, Long> {

    @Query("SELECT i.cantidad FROM InventarioCentroJpaEntity i " +
           "WHERE i.distributionCenterId = :centerId AND i.productId = :productId")
    Optional<Integer> findCantidad(Long centerId, Long productId);

    Optional<InventarioCentroJpaEntity> findByDistributionCenterIdAndProductId(Long distributionCenterId, Long productId);
}
