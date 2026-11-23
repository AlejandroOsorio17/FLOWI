package com.example.demo1.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo1.domain.Store;
import com.example.demo1.domain.port.out.StoreRepository;
import com.example.demo1.domain.exception.DuplicateResourceException;

@Service
public class StoreService {
    private final StoreRepository repository;

    public StoreService(StoreRepository repository) {
        this.repository = repository;
    }

    public Store registerStore(Store store) {
        validate(store);
        if (repository.existsByCode(store.getCode().trim())) {
            throw new DuplicateResourceException("Store code already exists");
        }
        store.setCode(store.getCode().trim());
        return repository.save(store);
    }

    public List<Store> listStores() {
        return repository.findAll();
    }

    private void validate(Store store) {
        if (store == null || isBlank(store.getCode()) || isBlank(store.getName())
                || isBlank(store.getAddress()) || isBlank(store.getCity())) {
            throw new IllegalArgumentException("Code, name, address and city are required");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
