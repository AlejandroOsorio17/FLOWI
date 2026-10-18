package com.example.demo1.infrastructure.adapter.in.web.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class SaleRequest {

    @NotNull(message = "El id de la tienda es obligatorio")
    private Long storeId;

    @NotEmpty(message = "La venta debe tener al menos un ítem")
    @Valid   // activa la validación en cascada sobre cada SaleItemRequest
    private List<SaleItemRequest> items;

    public SaleRequest() {}

    public Long getStoreId()                      { return storeId; }
    public void setStoreId(Long storeId)          { this.storeId = storeId; }
    public List<SaleItemRequest> getItems()       { return items; }
    public void setItems(List<SaleItemRequest> i) { this.items = i; }
}