package com.example.demo1.infrastructure.adapter.out.persistence;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.example.demo1.domain.port.out.InventoryRepository;

@Component
public class InventoryRepositoryImpl implements InventoryRepository {

    private final InventarioJpaRepository jpaRepository;

    public InventoryRepositoryImpl(InventarioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Integer> findStock(Long storeId, Long productId) {
        return jpaRepository.findCantidad(storeId, productId);
    }

    @Override
    public void decrementStock(Long storeId, Long productId, int cantidad) {
        jpaRepository.decrementCantidad(storeId, productId, cantidad);
    }
}