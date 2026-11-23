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

import com.example.demo1.application.service.DistributionCenterService;
import com.example.demo1.domain.DistributionCenter;
import com.example.demo1.domain.port.out.DistributionCenterRepository;
import com.example.demo1.domain.exception.DuplicateResourceException;

@ExtendWith(MockitoExtension.class)
class DistributionCenterServiceTest {
    @Mock
    private DistributionCenterRepository repository;

    @InjectMocks
    private DistributionCenterService service;

    @Test
    void shouldRegisterAndListDistributionCenters() {
        DistributionCenter center = new DistributionCenter(null, "DC-001", "Main Center",
                "Calle 80 No. 100-20", "Bogota");
        DistributionCenter saved = new DistributionCenter(1L, "DC-001", "Main Center",
                "Calle 80 No. 100-20", "Bogota");
        when(repository.existsByCode("DC-001")).thenReturn(false);
        when(repository.save(center)).thenReturn(saved);
        when(repository.findAll()).thenReturn(List.of(saved));

        assertEquals(saved, service.registerDistributionCenter(center));
        assertEquals(List.of(saved), service.listDistributionCenters());
        verify(repository).save(center);
    }

    @Test
    void shouldRejectDistributionCenterWithMissingRequiredFields() {
        DistributionCenter center = new DistributionCenter(null, "DC-001", "Main Center", "", "Bogota");

        assertThrows(IllegalArgumentException.class, () -> service.registerDistributionCenter(center));
    }

    @Test
    void shouldRejectDuplicateDistributionCenterCode() {
        DistributionCenter center = new DistributionCenter(null, "DC-001", "Main Center",
                "Address", "Bogota");
        when(repository.existsByCode("DC-001")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.registerDistributionCenter(center));
    }

    @Test
    void shouldReturnEmptyDistributionCenterListWhenNoCentersExist() {
        when(repository.findAll()).thenReturn(List.of());

        assertEquals(List.of(), service.listDistributionCenters());
    }
}
