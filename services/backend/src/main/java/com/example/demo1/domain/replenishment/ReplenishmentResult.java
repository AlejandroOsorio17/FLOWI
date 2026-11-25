package com.example.demo1.domain.replenishment;

/**
 * Resultado del cálculo de reposición para una combinación tienda-producto.
 *
 * @param deficit               unidades que faltan para alcanzar el stock objetivo (0 si no hay déficit)
 * @param recommendedQuantity   unidades a reponer, limitadas por el stock del centro si se conoce
 * @param dailyDemand           unidades vendidas por día en la ventana de demanda
 * @param coverageDays          días que cubre el stock actual; null si no hay demanda
 * @param priority              prioridad de reposición; null si no hay déficit
 * @param requiresReplenishment true si hay déficit
 */
public record ReplenishmentResult(int deficit,
                                  int recommendedQuantity,
                                  double dailyDemand,
                                  Double coverageDays,
                                  ReplenishmentPriority priority,
                                  boolean requiresReplenishment) {
}
