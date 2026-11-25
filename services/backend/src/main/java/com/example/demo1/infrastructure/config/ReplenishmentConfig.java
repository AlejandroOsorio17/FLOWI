package com.example.demo1.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo1.domain.replenishment.ReplenishmentCalculator;
import com.example.demo1.domain.replenishment.ReplenishmentParams;

/**
 * Expone como beans los parámetros y el calculador de reposición.
 * Los valores por defecto coinciden con application.properties, de modo que
 * el despliegue (p. ej. Render) funciona sin definir variables nuevas.
 */
@Configuration
public class ReplenishmentConfig {

    @Bean
    public ReplenishmentParams replenishmentParams(
            @Value("${replenishment.demand-window-days:30}") int demandWindowDays,
            @Value("${replenishment.coverage-days:7}") int coverageDays,
            @Value("${replenishment.high-priority-days:3}") int highPriorityDays,
            @Value("${replenishment.medium-priority-days:7}") int mediumPriorityDays) {
        return new ReplenishmentParams(demandWindowDays, coverageDays, highPriorityDays, mediumPriorityDays);
    }

    @Bean
    public ReplenishmentCalculator replenishmentCalculator(ReplenishmentParams params) {
        return new ReplenishmentCalculator(params);
    }
}
