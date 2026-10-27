package com.example.demo1.infrastructure.adapter.out.persistence;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.demo1.domain.Store;
import com.example.demo1.domain.port.out.StoreRepository;

@Component
public class StoreRepositoryImpl implements StoreRepository {
    private final StoreJpaRepository repository;

    public StoreRepositoryImpl(StoreJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Store save(Store store) {
        return toDomain(repository.save(toEntity(store)));
    }

    @Override
    public List<Store> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsByCode(String code) {
        return repository.existsByCode(code);
    }

    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    private StoreJpaEntity toEntity(Store store) {
        return new StoreJpaEntity(store.getId(), store.getCode(), store.getName(),
                store.getAddress(), store.getCity());
    }

    private Store toDomain(StoreJpaEntity entity) {
        return new Store(entity.getId(), entity.getCode(), entity.getName(),
                entity.getAddress(), entity.getCity());
    }
}
