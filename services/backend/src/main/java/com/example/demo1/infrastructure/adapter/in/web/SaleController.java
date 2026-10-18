package com.example.demo1.infrastructure.adapter.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo1.application.service.SaleService;
import com.example.demo1.domain.Sale;
import com.example.demo1.infrastructure.adapter.in.web.dto.SaleRequest;
import com.example.demo1.infrastructure.adapter.in.web.dto.SaleResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    /**
     * POST /api/sales
     * Registra una venta y descuenta el stock automáticamente.
     *
     * Body ejemplo:
     * {
     *   "storeId": 1,
     *   "items": [
     *     { "productId": 2, "cantidad": 3 },
     *     { "productId": 5, "cantidad": 1 }
     *   ]
     * }
     */
    @PostMapping
    public ResponseEntity<SaleResponse> createSale(@Valid @RequestBody SaleRequest request) {
        Sale sale = saleService.registerSale(request);

        SaleResponse response = new SaleResponse(
                sale.getId(),
                sale.getFecha(),
                sale.getStoreId(),
                sale.getTotal(),
                sale.getEstado());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}