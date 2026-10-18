package com.example.demo1.application.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo1.domain.InsufficientStockException;
import com.example.demo1.domain.Sale;
import com.example.demo1.domain.SaleItem;
import com.example.demo1.domain.port.out.InventoryRepository;
import com.example.demo1.domain.port.out.ProductRepository;
import com.example.demo1.domain.port.out.SaleRepository;
import com.example.demo1.infrastructure.adapter.in.web.dto.SaleItemRequest;
import com.example.demo1.infrastructure.adapter.in.web.dto.SaleRequest;

@Service
public class SaleService {

    private final SaleRepository      saleRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductRepository   productRepository;

    public SaleService(SaleRepository saleRepository,
                       InventoryRepository inventoryRepository,
                       ProductRepository productRepository) {
        this.saleRepository      = saleRepository;
        this.inventoryRepository = inventoryRepository;
        this.productRepository   = productRepository;
    }

    /**
     * Registra una venta y descuenta el stock en un solo bloque transaccional.
     * Si cualquier producto no tiene stock suficiente, se hace rollback completo.
     */
    @Transactional
    public Sale registerSale(SaleRequest request) {

        Long storeId = request.getStoreId();

        // 1. Verificar stock ANTES de modificar nada (fail-fast)
        for (SaleItemRequest itemReq : request.getItems()) {
            int disponible = inventoryRepository
                    .findStock(storeId, itemReq.getProductId())
                    .orElse(0);

            if (disponible < itemReq.getCantidad()) {
                throw new InsufficientStockException(
                        itemReq.getProductId(), disponible, itemReq.getCantidad());
            }
        }

        // 2. Construir items de dominio y calcular total
        List<SaleItem> items = request.getItems().stream()
                .map(req -> {
                    // Obtener precio actual del producto
                    BigDecimal precio = productRepository.findAll().stream()
                            .filter(p -> p.getId().equals(req.getProductId()))
                            .findFirst()
                            .map(p -> BigDecimal.valueOf(p.getPrice()))
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "Producto no encontrado: " + req.getProductId()));

                    return new SaleItem(null, req.getProductId(), req.getCantidad(), precio);
                })
                .collect(Collectors.toList());

        BigDecimal total = items.stream()
                .map(i -> i.getPrecioUnitario().multiply(BigDecimal.valueOf(i.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. Persistir la venta
        Sale sale = new Sale(null, LocalDateTime.now(), storeId, items, total, "COMPLETADA");
        Sale savedSale = saleRepository.save(sale);

        // 4. Descontar stock (dentro de la misma transacción)
        for (SaleItemRequest itemReq : request.getItems()) {
            inventoryRepository.decrementStock(
                    storeId, itemReq.getProductId(), itemReq.getCantidad());
        }

        return savedSale;
    }
}
