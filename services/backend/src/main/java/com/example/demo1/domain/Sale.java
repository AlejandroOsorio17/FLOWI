package com.example.demo1.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Sale {
    private Long id;
    private LocalDateTime fecha;
    private Long storeId;
    private List<SaleItem> items;
    private BigDecimal total;
    private String estado;

    public Sale() {}

    public Sale(Long id, LocalDateTime fecha, Long storeId,
                List<SaleItem> items, BigDecimal total, String estado) {
        this.id     = id;
        this.fecha  = fecha;
        this.storeId = storeId;
        this.items  = items;
        this.total  = total;
        this.estado = estado;
    }

    public Long getId()                { return id; }
    public void setId(Long id)         { this.id = id; }
    public LocalDateTime getFecha()    { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public Long getStoreId()           { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }
    public List<SaleItem> getItems()   { return items; }
    public void setItems(List<SaleItem> items) { this.items = items; }
    public BigDecimal getTotal()       { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public String getEstado()          { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}