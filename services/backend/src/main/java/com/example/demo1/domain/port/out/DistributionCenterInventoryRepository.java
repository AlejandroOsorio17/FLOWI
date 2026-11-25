package com.example.demo1.domain.port.out;

import java.util.Optional;

public interface DistributionCenterInventoryRepository {
    /** Retorna el stock actual del producto en el centro de distribución. */
    Optional<Integer> findStock(Long centerId, Long productId);

    /** Fija la cantidad de (centerId, productId); crea la fila si no existe. */
    void upsertQuantity(Long centerId, Long productId, int quantity);
}
