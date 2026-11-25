package com.example.demo1.domain.replenishment;

/** Stock de un producto en un centro de distribución. */
public record CenterStockLevel(Long centerId,
                               String centerName,
                               String city,
                               Long productId,
                               int stock) {
}
