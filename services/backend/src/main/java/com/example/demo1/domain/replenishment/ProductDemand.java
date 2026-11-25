package com.example.demo1.domain.replenishment;

/** Unidades vendidas de un producto en una tienda dentro de una ventana de tiempo. */
public record ProductDemand(Long productId, Long unitsSold) {
}
