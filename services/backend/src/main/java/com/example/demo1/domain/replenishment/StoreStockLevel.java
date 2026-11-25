package com.example.demo1.domain.replenishment;

/** Stock actual y mínimo de un producto en una tienda. */
public record StoreStockLevel(Long productId,
                              String productName,
                              String productCode,
                              int currentStock,
                              int minimumStock) {
}
