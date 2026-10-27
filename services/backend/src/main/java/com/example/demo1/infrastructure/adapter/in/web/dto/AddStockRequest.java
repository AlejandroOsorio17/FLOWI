package com.example.demo1.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AddStockRequest {

    @NotNull(message = "storeId es obligatorio")
    private Long storeId;

    @NotNull(message = "productId es obligatorio")
    private Long productId;

    @NotNull(message = "quantity es obligatorio")
    @Min(value = 1, message = "quantity debe ser mayor o igual a 1")
    private Integer quantity;

    public AddStockRequest() {
    }

    public AddStockRequest(Long storeId, Long productId, Integer quantity) {
        this.storeId = storeId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
