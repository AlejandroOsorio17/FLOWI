package com.example.demo1.infrastructure.adapter.out.persistence;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.demo1.domain.Sale;
import com.example.demo1.domain.SaleItem;
import com.example.demo1.domain.port.out.SaleRepository;

@Component
public class SaleRepositoryImpl implements SaleRepository {

    private final VentaJpaRepository ventaRepo;

    public SaleRepositoryImpl(VentaJpaRepository ventaRepo) {
        this.ventaRepo = ventaRepo;
    }

    @Override
    public Sale save(Sale sale) {
        VentaJpaEntity entity = toEntity(sale);
        VentaJpaEntity saved  = ventaRepo.save(entity);
        return toDomain(saved);
    }

    private VentaJpaEntity toEntity(Sale sale) {
        VentaJpaEntity e = new VentaJpaEntity();
        e.setFecha(sale.getFecha());
        e.setStoreId(sale.getStoreId());
        e.setTotal(sale.getTotal());
        e.setEstado(sale.getEstado());

        List<DetalleVentaJpaEntity> detalles = sale.getItems().stream().map(item -> {
            DetalleVentaJpaEntity d = new DetalleVentaJpaEntity();
            d.setVenta(e);
            d.setProductId(item.getProductId());
            d.setCantidad(item.getCantidad());
            d.setPrecioUnitario(item.getPrecioUnitario());
            return d;
        }).collect(Collectors.toList());

        e.setDetalles(detalles);
        return e;
    }

    private Sale toDomain(VentaJpaEntity e) {
        List<SaleItem> items = e.getDetalles() == null ? List.of() :
                e.getDetalles().stream()
                        .map(d -> new SaleItem(d.getId(), d.getProductId(),
                                               d.getCantidad(), d.getPrecioUnitario()))
                        .collect(Collectors.toList());
        return new Sale(e.getId(), e.getFecha(), e.getStoreId(), items, e.getTotal(), e.getEstado());
    }
}