package com.example.demo1.infrastructure.adapter.out.persistence;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "ventas")
public class VentaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fecha;

    @Column(name = "store_id")
    private Long storeId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column(length = 20)
    private String estado;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVentaJpaEntity> detalles;

    public VentaJpaEntity() {}

    public Long getId()                                      { return id; }
    public void setId(Long id)                               { this.id = id; }
    public LocalDateTime getFecha()                          { return fecha; }
    public void setFecha(LocalDateTime fecha)                { this.fecha = fecha; }
    public Long getStoreId()                                 { return storeId; }
    public void setStoreId(Long storeId)                     { this.storeId = storeId; }
    public BigDecimal getTotal()                             { return total; }
    public void setTotal(BigDecimal total)                   { this.total = total; }
    public String getEstado()                                { return estado; }
    public void setEstado(String estado)                     { this.estado = estado; }
    public List<DetalleVentaJpaEntity> getDetalles()        { return detalles; }
    public void setDetalles(List<DetalleVentaJpaEntity> d)  { this.detalles = d; }
}