package com.example.demo1.infrastructure.adapter.out.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "products")
public class ProductJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String name;
    private String description;
    private String category;
    private String unit;
    private Integer minimum;
    private Double price;

    public ProductJpaEntity() {
    }

    public ProductJpaEntity(Long id, String code, String name, String description, String category,
            String unit, Integer minimum, Double price) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.category = category;
        this.unit = unit;
        this.minimum = minimum;
        this.price = price;
    }

    /**
     * Overload retrocompatible: deja category/unit/minimum en null
     * (la query enriquecida hace COALESCE a valores por defecto).
     */
    public ProductJpaEntity(Long id, String code, String name, String description, Double price) {
        this(id, code, name, description, null, null, null, price);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public Integer getMinimum() { return minimum; }
    public void setMinimum(Integer minimum) { this.minimum = minimum; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
}
