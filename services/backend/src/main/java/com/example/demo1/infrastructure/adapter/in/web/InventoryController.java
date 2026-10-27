package com.example.demo1.infrastructure.adapter.in.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo1.application.service.InventoryService;
import com.example.demo1.domain.InventoryItem;
import com.example.demo1.infrastructure.adapter.in.web.dto.InventoryUpsertRequest;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/{storeId}")
    public List<InventoryItem> getInventory(@PathVariable Long storeId) {
        return inventoryService.getInventoryByStore(storeId);
    }

    /**
     * Endpoint enriquecido para el frontend:
     * devuelve {id,name,sku,category,detail,price,quantity,minimum}
     */
    @GetMapping("/{storeId}/enriched")
    public List<com.example.demo1.infrastructure.adapter.in.web.dto.EnrichedInventoryItemResponse> getEnrichedInventory(
            @PathVariable Long storeId) {
        return inventoryService.getEnrichedInventoryByStore(storeId);
    }

    @PutMapping("/{storeId}")
    public void upsertInventory(@PathVariable Long storeId, @RequestBody InventoryUpsertRequest request) {
        inventoryService.upsertInventory(storeId, request.getProductId(), request.getQuantity());
    }

    @GetMapping("/{storeId}/low-stock")
    public List<InventoryItem> getLowStock(
            @PathVariable Long storeId,
            @RequestParam(defaultValue = "10") int threshold) {
        return inventoryService.getLowStockByStore(storeId, threshold);
    }
}
