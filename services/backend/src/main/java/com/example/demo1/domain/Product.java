package com.example.demo1.domain;

public class Product {
    private Long id;
    private String code;
    private String name;
    private String description;
    private Double price;
    private String category;
    private String unit;
    private Integer minimumStock;

    public Product() {
    }

    /**
     * Constructor retrocompatible sin category/unit/minimumStock.
     */
    public Product(Long id, String code, String name, String description, Double price) {
        this(id, code, name, description, price, null, null, null);
    }

    public Product(Long id, String code, String name, String description, Double price,
            String category, String unit, Integer minimumStock) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.unit = unit;
        this.minimumStock = minimumStock;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getMinimumStock() {
        return minimumStock;
    }

    public void setMinimumStock(Integer minimumStock) {
        this.minimumStock = minimumStock;
    }
}
