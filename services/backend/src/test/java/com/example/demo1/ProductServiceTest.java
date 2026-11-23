package com.example.demo1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo1.application.service.ProductService;
import com.example.demo1.domain.Product;
import com.example.demo1.domain.port.out.ProductRepository;
import com.example.demo1.domain.exception.DuplicateResourceException;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductService service;

    @Test
    void shouldRegisterAndListProducts() {
        Product product = new Product(null, "P-001", "T-Shirt", "Cotton", 89900.0);
        Product saved = new Product(1L, "P-001", "T-Shirt", "Cotton", 89900.0);
        when(repository.existsByCode("P-001")).thenReturn(false);
        when(repository.save(product)).thenReturn(saved);
        when(repository.findAll()).thenReturn(List.of(saved));

        assertEquals(saved, service.registerProduct(product));
        assertEquals(List.of(saved), service.listProducts());
        verify(repository).save(product);
    }

    @Test
    void shouldRejectProductWithMissingRequiredFields() {
        Product product = new Product(null, "", "T-Shirt", null, 89900.0);

        assertThrows(IllegalArgumentException.class, () -> service.registerProduct(product));
    }

    @Test
    void shouldRejectDuplicateProductCode() {
        Product product = new Product(null, "P-001", "T-Shirt", "Cotton", 89900.0);
        when(repository.existsByCode("P-001")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.registerProduct(product));
    }

    @Test
    void shouldReturnEmptyProductListWhenNoProductsExist() {
        when(repository.findAll()).thenReturn(List.of());

        assertEquals(List.of(), service.listProducts());
    }
}
