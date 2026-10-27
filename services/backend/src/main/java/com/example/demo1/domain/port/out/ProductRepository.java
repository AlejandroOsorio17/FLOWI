package com.example.demo1.domain.port.out;

import java.util.List;
import java.util.Optional;

import com.example.demo1.domain.Product;

public interface ProductRepository {
    Product save(Product product);
    List<Product> findAll();
    boolean existsByCode(String code);
    boolean existsById(Long id);
    Optional<Product> findById(Long id);
}
