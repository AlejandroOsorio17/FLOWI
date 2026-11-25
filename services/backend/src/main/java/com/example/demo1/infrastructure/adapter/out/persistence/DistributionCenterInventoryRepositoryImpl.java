package com.example.demo1.infrastructure.adapter.out.persistence;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.example.demo1.domain.port.out.DistributionCenterInventoryRepository;

@Component
public class DistributionCenterInventoryRepositoryImpl implements DistributionCenterInventoryRepository {

    private final InventarioCentroJpaRepository jpaRepository;

    public DistributionCenterInventoryRepositoryImpl(InventarioCentroJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Integer> findStock(Long centerId, Long productId) {
        return jpaRepository.findCantidad(centerId, productId);
    }

    @Override
    public void upsertQuantity(Long centerId, Long productId, int quantity) {
        // Upsert por distribution_center_id + product_id (hay unique constraint).
        var entity = jpaRepository.findByDistributionCenterIdAndProductId(centerId, productId)
                .orElseGet(() -> {
                    var created = new InventarioCentroJpaEntity();
                    created.setDistributionCenterId(centerId);
                    created.setProductId(productId);
                    return created;
                });
        entity.setCantidad(quantity);
        jpaRepository.save(entity);
    }
}
