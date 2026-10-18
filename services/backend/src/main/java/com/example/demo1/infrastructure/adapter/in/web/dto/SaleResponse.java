package com.example.demo1.infrastructure.adapter.in.web.dto;


import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SaleResponse {
    private Long id;
    private LocalDateTime fecha;
    private Long storeId;
    private BigDecimal total;
    private String estado;

    public SaleResponse() {}

    public SaleResponse(Long id, LocalDateTime fecha, Long storeId,
                        BigDecimal total, String estado) {
        this.id      = id;
        this.fecha   = fecha;
        this.storeId = storeId;
        this.total   = total;
        this.estado  = estado;
    }

    public Long getId()               { return id; }
    public LocalDateTime getFecha()   { return fecha; }
    public Long getStoreId()          { return storeId; }
    public BigDecimal getTotal()      { return total; }
    public String getEstado()         { return estado; }
}