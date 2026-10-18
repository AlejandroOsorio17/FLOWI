package com.example.demo1.domain;

public class InventoryItem {

    private final Long productId;
    private final String productName;
    private final Integer cantidad;

    public InventoryItem(Long productId, String productName, Integer cantidad) {
        this.productId = productId;
        this.productName = productName;
        this.cantidad = cantidad;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getCantidad() {
        return cantidad;
    }
}
