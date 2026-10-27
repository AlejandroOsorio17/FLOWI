package com.example.demo1.infrastructure.adapter.in.web.dto;

public class InventoryUpsertRequest {
    private Long productId;
    private Integer quantity;

    public InventoryUpsertRequest() {}

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
