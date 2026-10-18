package com.example.demo1.domain;

/** Excepción de dominio: stock insuficiente para completar la venta. */
public class InsufficientStockException extends RuntimeException {
    private final Long productId;
    private final int disponible;
    private final int solicitado;

    public InsufficientStockException(Long productId, int disponible, int solicitado) {
        super(String.format(
            "Stock insuficiente para producto id=%d: disponible=%d, solicitado=%d",
            productId, disponible, solicitado));
        this.productId  = productId;
        this.disponible = disponible;
        this.solicitado = solicitado;
    }

    public Long getProductId()  { return productId; }
    public int getDisponible()  { return disponible; }
    public int getSolicitado()  { return solicitado; }
}