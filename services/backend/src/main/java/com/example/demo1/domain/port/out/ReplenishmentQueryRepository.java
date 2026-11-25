package com.example.demo1.domain.port.out;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.example.demo1.domain.replenishment.CenterStockLevel;
import com.example.demo1.domain.replenishment.ProductDemand;
import com.example.demo1.domain.replenishment.StoreStockLevel;

public interface ReplenishmentQueryRepository {

    /** Unidades vendidas por producto en la tienda (ventas COMPLETADA con fecha >= since), ordenado por producto. */
    List<ProductDemand> findDemandByStore(Long storeId, LocalDateTime since);

    /** Unidades vendidas de un producto en la tienda desde 'since'; 0 si no hay ventas. */
    long findUnitsSold(Long storeId, Long productId, LocalDateTime since);

    /** Stock y mínimo de todos los productos en la tienda (0 si no hay fila de inventario), ordenado por producto. */
    List<StoreStockLevel> findStoreStockLevels(Long storeId);

    /** Stock y mínimo de un producto en la tienda; vacío solo si el producto no existe. */
    Optional<StoreStockLevel> findStoreStockLevel(Long storeId, Long productId);

    /** Stock de cada producto en cada centro (0 si no hay fila), ordenado por centro y producto. */
    List<CenterStockLevel> findCenterStockLevels();

    /** Stock del producto en todos los centros (0 si no hay fila), ordenado por centro. */
    List<CenterStockLevel> findCenterStockLevelsByProduct(Long productId);
}
