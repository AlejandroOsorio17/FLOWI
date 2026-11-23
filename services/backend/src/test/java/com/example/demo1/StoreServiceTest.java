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

import com.example.demo1.application.service.StoreService;
import com.example.demo1.domain.Store;
import com.example.demo1.domain.port.out.StoreRepository;
import com.example.demo1.domain.exception.DuplicateResourceException;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {
    @Mock
    private StoreRepository repository;

    @InjectMocks
    private StoreService service;

    @Test
    void shouldRegisterAndListStores() {
        Store store = new Store(null, "S-001", "Main Store", "Carrera 11 No. 82-71", "Bogota");
        Store saved = new Store(1L, "S-001", "Main Store", "Carrera 11 No. 82-71", "Bogota");
        when(repository.existsByCode("S-001")).thenReturn(false);
        when(repository.save(store)).thenReturn(saved);
        when(repository.findAll()).thenReturn(List.of(saved));

        assertEquals(saved, service.registerStore(store));
        assertEquals(List.of(saved), service.listStores());
        verify(repository).save(store);
    }

    @Test
    void shouldRejectStoreWithMissingRequiredFields() {
        Store store = new Store(null, "S-001", "", "Address", "Bogota");

        assertThrows(IllegalArgumentException.class, () -> service.registerStore(store));
    }

    @Test
    void shouldRejectDuplicateStoreCode() {
        Store store = new Store(null, "S-001", "Main Store", "Address", "Bogota");
        when(repository.existsByCode("S-001")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.registerStore(store));
    }

    @Test
    void shouldReturnEmptyStoreListWhenNoStoresExist() {
        when(repository.findAll()).thenReturn(List.of());

        assertEquals(List.of(), service.listStores());
    }
}
