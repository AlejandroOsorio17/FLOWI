package com.example.demo1.domain.port.out;
import com.example.demo1.domain.Sale;

public interface SaleRepository {
    Sale save(Sale sale);
}