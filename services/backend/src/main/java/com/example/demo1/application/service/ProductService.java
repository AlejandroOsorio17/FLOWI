package com.example.demo1.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo1.domain.Product;
import com.example.demo1.domain.port.out.ProductRepository;
import com.example.demo1.infrastructure.adapter.in.web.DuplicateResourceException;

@Service
public class ProductService {
    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public Product registerProduct(Product product) {
        validate(product);
        if (repository.existsByCode(product.getCode().trim())) {
            throw new DuplicateResourceException("Product code already exists");
        }
        product.setCode(product.getCode().trim());
        return repository.save(product);
    }

    public List<Product> listProducts() {
        return repository.findAll();
    }

    private void validate(Product product) {
        if (product == null || isBlank(product.getCode()) || isBlank(product.getName())
                || isBlank(product.getDescription())
                || product.getPrice() == null || product.getPrice() < 0) {
            throw new IllegalArgumentException("Code, name, description and a non-negative price are required");
        }
        if (product.getMinimumStock() != null && product.getMinimumStock() < 0) {
            throw new IllegalArgumentException("Minimum stock must be greater than or equal to zero");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
