package com.example.demo1.domain.port.out;

import java.util.Optional;

public interface InventoryRepository {
    /** Retorna el stock actual del producto en la tienda. */
    Optional<Integer> findStock(Long storeId, Long productId);

    /** Descuenta 'cantidad' unidades. Lanza excepción si no hay fila. */
    void decrementStock(Long storeId, Long productId, int cantidad);
}