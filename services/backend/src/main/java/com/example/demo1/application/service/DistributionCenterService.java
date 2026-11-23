package com.example.demo1.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo1.domain.DistributionCenter;
import com.example.demo1.domain.port.out.DistributionCenterRepository;
import com.example.demo1.domain.exception.DuplicateResourceException;

@Service
public class DistributionCenterService {
    private final DistributionCenterRepository repository;

    public DistributionCenterService(DistributionCenterRepository repository) {
        this.repository = repository;
    }

    public DistributionCenter registerDistributionCenter(DistributionCenter distributionCenter) {
        validate(distributionCenter);
        if (repository.existsByCode(distributionCenter.getCode().trim())) {
            throw new DuplicateResourceException("Distribution center code already exists");
        }
        distributionCenter.setCode(distributionCenter.getCode().trim());
        return repository.save(distributionCenter);
    }

    public List<DistributionCenter> listDistributionCenters() {
        return repository.findAll();
    }

    private void validate(DistributionCenter distributionCenter) {
        if (distributionCenter == null || isBlank(distributionCenter.getCode())
                || isBlank(distributionCenter.getName()) || isBlank(distributionCenter.getAddress())
                || isBlank(distributionCenter.getCity())) {
            throw new IllegalArgumentException("Code, name, address and city are required");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
