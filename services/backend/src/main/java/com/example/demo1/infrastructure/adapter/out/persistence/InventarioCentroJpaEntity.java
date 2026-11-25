package com.example.demo1.infrastructure.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "inventario_centro",
       uniqueConstraints = @UniqueConstraint(columnNames = {"distribution_center_id", "product_id"}))
public class InventarioCentroJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "distribution_center_id")
    private Long distributionCenterId;

    @Column(name = "product_id")
    private Long productId;

    private Integer cantidad;

    public InventarioCentroJpaEntity() {}

    public Long getId()                        { return id; }
    public void setId(Long id)                 { this.id = id; }
    public Long getDistributionCenterId()      { return distributionCenterId; }
    public void setDistributionCenterId(Long d){ this.distributionCenterId = d; }
    public Long getProductId()                 { return productId; }
    public void setProductId(Long p)           { this.productId = p; }
    public Integer getCantidad()               { return cantidad; }
    public void setCantidad(Integer c)         { this.cantidad = c; }
}
