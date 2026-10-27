package com.example.demo1.infrastructure.adapter.in.web;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo1.application.service.ProductService;
import com.example.demo1.domain.Product;
import com.example.demo1.infrastructure.adapter.in.web.dto.ProductRequest;
import com.example.demo1.infrastructure.adapter.in.web.dto.ProductResponse;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> registerProduct(@RequestBody ProductRequest request) {
        Product product = service.registerProduct(new Product(null, request.getCode(), request.getName(),
                request.getDescription(), request.getPrice(), request.getCategory(),
                request.getUnit(), request.getMinimumStock()));
        return ResponseEntity.status(201).body(toResponse(product));
    }

    @GetMapping
    public ResponseEntity<?> listProducts() {
        List<Product> products = service.listProducts();
        if (products.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "No records found"));
        }
        return ResponseEntity.ok(products.stream().map(this::toResponse).toList());
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(product.getId(), product.getCode(), product.getName(),
                product.getDescription(), product.getPrice(), product.getCategory(),
                product.getUnit(), product.getMinimumStock());
    }
}
