package com.example.demo1.domain;

import java.math.BigDecimal;

public class SaleItem {
    private Long id;
    private Long productId;
    private Integer cantidad;
    private BigDecimal precioUnitario;

    public SaleItem() {}

    public SaleItem(Long id, Long productId, Integer cantidad, BigDecimal precioUnitario) {
        this.id              = id;
        this.productId       = productId;
        this.cantidad        = cantidad;
        this.precioUnitario  = precioUnitario;
    }

    public Long getId()                          { return id; }
    public void setId(Long id)                   { this.id = id; }
    public Long getProductId()                   { return productId; }
    public void setProductId(Long productId)     { this.productId = productId; }
    public Integer getCantidad()                 { return cantidad; }
    public void setCantidad(Integer cantidad)    { this.cantidad = cantidad; }
    public BigDecimal getPrecioUnitario()        { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal p)  { this.precioUnitario = p; }
}