package com.example.demo1.infrastructure.adapter.out.persistence;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.example.demo1.domain.port.out.ReplenishmentQueryRepository;
import com.example.demo1.domain.replenishment.CenterStockLevel;
import com.example.demo1.domain.replenishment.ProductDemand;
import com.example.demo1.domain.replenishment.StoreStockLevel;

@Component
public class ReplenishmentQueryRepositoryImpl implements ReplenishmentQueryRepository {

    private final ReplenishmentJpaRepository jpaRepository;

    public ReplenishmentQueryRepositoryImpl(ReplenishmentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<ProductDemand> findDemandByStore(Long storeId, LocalDateTime since) {
        return jpaRepository.findDemandByStore(storeId, since);
    }

    @Override
    public long findUnitsSold(Long storeId, Long productId, LocalDateTime since) {
        Long units = jpaRepository.findUnitsSold(storeId, productId, since);
        return units == null ? 0L : units;
    }

    @Override
    public List<StoreStockLevel> findStoreStockLevels(Long storeId) {
        return jpaRepository.findStoreStockLevels(storeId);
    }

    @Override
    public Optional<StoreStockLevel> findStoreStockLevel(Long storeId, Long productId) {
        return jpaRepository.findStoreStockLevel(storeId, productId);
    }

    @Override
    public List<CenterStockLevel> findCenterStockLevels() {
        return jpaRepository.findCenterStockLevels();
    }

    @Override
    public List<CenterStockLevel> findCenterStockLevelsByProduct(Long productId) {
        return jpaRepository.findCenterStockLevelsByProduct(productId);
    }
}
