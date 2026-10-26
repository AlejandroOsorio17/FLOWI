package com.example.demo1.infrastructure.adapter.in.web.dto;

public class EnrichedInventoryItemResponse {
    private final Long id;
    private final String name;
    private final String sku;
    private final String category;
    private final String detail;
    private final Double price;
    private final Integer quantity;
    private final Integer minimum;

    public EnrichedInventoryItemResponse(
            Long id,
            String name,
            String sku,
            String category,
            String detail,
            Double price,
            Integer quantity,
            Integer minimum) {
        this.id = id;
        this.name = name;
        this.sku = sku;
        this.category = category;
        this.detail = detail;
        this.price = price;
        this.quantity = quantity;
        this.minimum = minimum;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSku() {
        return sku;
    }

    public String getCategory() {
        return category;
    }

    public String getDetail() {
        return detail;
    }

    public Double getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Integer getMinimum() {
        return minimum;
    }
}
