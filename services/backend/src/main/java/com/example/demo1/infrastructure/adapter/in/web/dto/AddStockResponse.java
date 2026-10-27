package com.example.demo1.infrastructure.adapter.in.web.dto;

public class AddStockResponse {
    private Long storeId;
    private Long productId;
    private String productName;
    private Integer added;
    private Integer quantity;

    public AddStockResponse() {
    }

    public AddStockResponse(Long storeId, Long productId, String productName, Integer added, Integer quantity) {
        this.storeId = storeId;
        this.productId = productId;
        this.productName = productName;
        this.added = added;
        this.quantity = quantity;
    }

    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public Integer getAdded() { return added; }
    public void setAdded(Integer added) { this.added = added; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
