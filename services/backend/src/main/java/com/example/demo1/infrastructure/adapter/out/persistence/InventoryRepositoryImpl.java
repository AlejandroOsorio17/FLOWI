package com.example.demo1.infrastructure.adapter.out.persistence;

import java.util.Optional;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.example.demo1.domain.port.out.InventoryRepository;
import com.example.demo1.domain.InventoryItem;
import com.example.demo1.infrastructure.adapter.in.web.dto.EnrichedInventoryItemResponse;

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

    @Override
    public List<InventoryItem> findInventoryByStore(Long storeId) {
        return jpaRepository.findInventoryByStore(storeId);
    }

    @Override
    public List<EnrichedInventoryItemResponse> findEnrichedInventoryByStore(Long storeId) {
        return jpaRepository.findEnrichedInventoryByStore(storeId);
    }

    @Override
    public void upsertQuantity(Long storeId, Long productId, int quantity) {
        // Upsert por store_id + product_id (hay unique constraint).
        var existing = jpaRepository.findByStoreIdAndProductId(storeId, productId);
        if (existing.isPresent()) {
            var entity = existing.get();
            entity.setCantidad(quantity);
            jpaRepository.save(entity);
        } else {
            var entity = new InventarioJpaEntity();
            entity.setStoreId(storeId);
            entity.setProductId(productId);
            entity.setCantidad(quantity);
            jpaRepository.save(entity);
        }
    }

    @Override
    public List<InventoryItem> findLowStockByStore(Long storeId, int threshold) {
        return jpaRepository.findLowStockByStore(storeId, threshold);
    }

    @Override
    public int addStock(Long storeId, Long productId, int quantity) {
        var existing = jpaRepository.findByStoreIdAndProductId(storeId, productId);
        if (existing.isPresent()) {
            var entity = existing.get();
            int newQuantity = entity.getCantidad() + quantity;
            entity.setCantidad(newQuantity);
            jpaRepository.save(entity);
            return newQuantity;
        }
        try {
            var entity = new InventarioJpaEntity();
            entity.setStoreId(storeId);
            entity.setProductId(productId);
            entity.setCantidad(quantity);
            jpaRepository.save(entity);
            return quantity;
        } catch (DataIntegrityViolationException ex) {
            // Carrera: otra petición creó la fila entre el findBy y el save. Reintenta como incremento.
            var raceExisting = jpaRepository.findByStoreIdAndProductId(storeId, productId)
                    .orElseThrow(() -> ex);
            int newQuantity = raceExisting.getCantidad() + quantity;
            raceExisting.setCantidad(newQuantity);
            jpaRepository.save(raceExisting);
            return newQuantity;
        }
    }
}
