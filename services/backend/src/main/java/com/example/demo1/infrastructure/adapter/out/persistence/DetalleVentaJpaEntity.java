package com.example.demo1.infrastructure.adapter.out.persistence;


import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "detalle_ventas")
public class DetalleVentaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id", nullable = false)
    private VentaJpaEntity venta;

    @Column(name = "product_id")
    private Long productId;

    private Integer cantidad;

    @Column(name = "precio_unitario", precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    public DetalleVentaJpaEntity() {}

    public Long getId()                            { return id; }
    public void setId(Long id)                     { this.id = id; }
    public VentaJpaEntity getVenta()               { return venta; }
    public void setVenta(VentaJpaEntity v)         { this.venta = v; }
    public Long getProductId()                     { return productId; }
    public void setProductId(Long p)               { this.productId = p; }
    public Integer getCantidad()                   { return cantidad; }
    public void setCantidad(Integer c)             { this.cantidad = c; }
    public BigDecimal getPrecioUnitario()          { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal p)    { this.precioUnitario = p; }
}