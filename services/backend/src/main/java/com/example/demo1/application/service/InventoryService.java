package com.example.demo1.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo1.domain.InventoryItem;
import com.example.demo1.domain.port.out.InventoryRepository;
import com.example.demo1.infrastructure.adapter.in.web.dto.EnrichedInventoryItemResponse;

import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public List<InventoryItem> getInventoryByStore(Long storeId) {
        return inventoryRepository.findInventoryByStore(storeId);
    }

    public List<EnrichedInventoryItemResponse> getEnrichedInventoryByStore(Long storeId) {
        return inventoryRepository.findEnrichedInventoryByStore(storeId);
    }

    @Transactional
    public void upsertInventory(Long storeId, Long productId, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        }
        inventoryRepository.upsertQuantity(storeId, productId, quantity);
    }

    public List<InventoryItem> getLowStockByStore(Long storeId, int threshold) {
        if (threshold < 0) {
            throw new IllegalArgumentException("El umbral no puede ser negativo");
        }
        return inventoryRepository.findLowStockByStore(storeId, threshold);
    }
}
