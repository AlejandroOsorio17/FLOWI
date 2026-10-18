package com.example.demo1.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class SaleItemRequest {

    @NotNull(message = "El id del producto es obligatorio")
    private Long productId;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a cero")
    @Min(value = 1, message = "Debe comprar al menos 1 unidad")
    private Integer cantidad;

    public SaleItemRequest() {}

    public Long getProductId()              { return productId; }
    public void setProductId(Long productId){ this.productId = productId; }
    public Integer getCantidad()            { return cantidad; }
    public void setCantidad(Integer c)      { this.cantidad = c; }
}
