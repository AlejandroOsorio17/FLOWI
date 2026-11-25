package com.example.demo1.domain.replenishment;

/**
 * Parámetros de la regla de reposición.
 *
 * @param demandWindowDays   días de ventas históricas usados para calcular la demanda diaria
 * @param coverageDays       días de demanda que debe cubrir el stock objetivo
 * @param highPriorityDays   cobertura (en días) por debajo de la cual la prioridad es ALTA
 * @param mediumPriorityDays cobertura (en días) por debajo de la cual la prioridad es MEDIA
 */
public record ReplenishmentParams(int demandWindowDays,
                                  int coverageDays,
                                  int highPriorityDays,
                                  int mediumPriorityDays) {

    public ReplenishmentParams {
        if (demandWindowDays <= 0) {
            throw new IllegalArgumentException("demandWindowDays debe ser mayor que 0");
        }
        if (coverageDays <= 0) {
            throw new IllegalArgumentException("coverageDays debe ser mayor que 0");
        }
        if (highPriorityDays <= 0) {
            throw new IllegalArgumentException("highPriorityDays debe ser mayor que 0");
        }
        if (mediumPriorityDays <= 0) {
            throw new IllegalArgumentException("mediumPriorityDays debe ser mayor que 0");
        }
        if (highPriorityDays >= mediumPriorityDays) {
            throw new IllegalArgumentException("highPriorityDays debe ser menor que mediumPriorityDays");
        }
    }
}
