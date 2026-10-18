package com.example.demo1.infrastructure.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "inventario",
       uniqueConstraints = @UniqueConstraint(columnNames = {"store_id", "product_id"}))
public class InventarioJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "store_id")
    private Long storeId;

    @Column(name = "product_id")
    private Long productId;

    private Integer cantidad;

    public InventarioJpaEntity() {}

    public Long getId()              { return id; }
    public void setId(Long id)       { this.id = id; }
    public Long getStoreId()         { return storeId; }
    public void setStoreId(Long s)   { this.storeId = s; }
    public Long getProductId()       { return productId; }
    public void setProductId(Long p) { this.productId = p; }
    public Integer getCantidad()     { return cantidad; }
    public void setCantidad(Integer c){ this.cantidad = c; }
}