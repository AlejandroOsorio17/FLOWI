package com.example.demo1.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo1.domain.InventoryItem;
import com.example.demo1.domain.Product;
import com.example.demo1.domain.port.out.InventoryRepository;
import com.example.demo1.domain.port.out.ProductRepository;
import com.example.demo1.domain.port.out.StoreRepository;
import com.example.demo1.infrastructure.adapter.in.web.ResourceNotFoundException;
import com.example.demo1.infrastructure.adapter.in.web.dto.AddStockResponse;
import com.example.demo1.infrastructure.adapter.in.web.dto.EnrichedInventoryItemResponse;

import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;

    public InventoryService(InventoryRepository inventoryRepository, StoreRepository storeRepository,
            ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.storeRepository = storeRepository;
        this.productRepository = productRepository;
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

    @Transactional
    public AddStockResponse addStock(Long storeId, Long productId, int quantity) {
        if (!storeRepository.existsById(storeId)) {
            throw new ResourceNotFoundException("No existe una tienda con id " + storeId);
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un producto con id " + productId));

        int newQuantity = inventoryRepository.addStock(storeId, productId, quantity);
        return new AddStockResponse(storeId, productId, product.getName(), quantity, newQuantity);
    }
}
