package com.example.demo1.domain.port.out;

import java.util.Optional;
import java.util.List;

import com.example.demo1.domain.InventoryItem;
import com.example.demo1.infrastructure.adapter.in.web.dto.EnrichedInventoryItemResponse;

public interface InventoryRepository {
    /** Retorna el stock actual del producto en la tienda. */
    Optional<Integer> findStock(Long storeId, Long productId);

    /** Descuenta 'cantidad' unidades. Lanza excepción si no hay fila. */
    void decrementStock(Long storeId, Long productId, int cantidad);

    List<InventoryItem> findInventoryByStore(Long storeId);

    List<EnrichedInventoryItemResponse> findEnrichedInventoryByStore(Long storeId);

    void upsertQuantity(Long storeId, Long productId, int quantity);

    List<InventoryItem> findLowStockByStore(Long storeId, int threshold);
}
