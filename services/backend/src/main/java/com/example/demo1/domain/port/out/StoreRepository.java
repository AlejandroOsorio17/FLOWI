package com.example.demo1.domain.port.out;

import java.util.List;

import com.example.demo1.domain.Store;

public interface StoreRepository {
    Store save(Store store);
    List<Store> findAll();
    boolean existsByCode(String code);
    boolean existsById(Long id);
}
