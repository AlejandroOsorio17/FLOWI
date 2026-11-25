package com.example.demo1.domain.replenishment;

/**
 * Reglas de cálculo de reposición. Lógica pura de dominio, sin estado mutable:
 * solo conserva los parámetros inmutables con los que fue construida.
 */
public class ReplenishmentCalculator {

    private final ReplenishmentParams params;

    public ReplenishmentCalculator(ReplenishmentParams params) {
        if (params == null) {
            throw new IllegalArgumentException("params no puede ser null");
        }
        this.params = params;
    }

    /**
     * @param currentStock stock actual en la tienda (>= 0)
     * @param minimumStock stock mínimo del producto (>= 0)
     * @param unitsSold    unidades vendidas en la ventana de demanda (>= 0)
     * @param centerStock  stock disponible en el centro de distribución; null si se desconoce.
     *                     Un valor negativo se trata como 0.
     */
    public ReplenishmentResult calculate(int currentStock, int minimumStock, int unitsSold, Integer centerStock) {
        if (currentStock < 0) {
            throw new IllegalArgumentException("currentStock no puede ser negativo");
        }
        if (minimumStock < 0) {
            throw new IllegalArgumentException("minimumStock no puede ser negativo");
        }
        if (unitsSold < 0) {
            throw new IllegalArgumentException("unitsSold no puede ser negativo");
        }

        double dailyDemand = (double) unitsSold / params.demandWindowDays();

        // ceil(dailyDemand * coverageDays) en aritmética entera para evitar errores de punto flotante.
        long projected = ceilDiv((long) unitsSold * params.coverageDays(), params.demandWindowDays());
        int target = Math.toIntExact(Math.max(minimumStock, projected));

        int deficit = Math.max(0, target - currentStock);
        boolean requiresReplenishment = deficit > 0;

        int recommendedQuantity = centerStock == null
                ? deficit
                : Math.min(deficit, Math.max(0, centerStock));

        Double coverageDays = dailyDemand > 0 ? currentStock / dailyDemand : null;

        ReplenishmentPriority priority = requiresReplenishment
                ? resolvePriority(currentStock, unitsSold)
                : null;

        return new ReplenishmentResult(deficit, recommendedQuantity, dailyDemand,
                coverageDays, priority, requiresReplenishment);
    }

    private ReplenishmentPriority resolvePriority(int currentStock, int unitsSold) {
        if (currentStock == 0) {
            return ReplenishmentPriority.ALTA;
        }
        if (unitsSold == 0) {
            // Sin demanda no hay cobertura calculable.
            return ReplenishmentPriority.BAJA;
        }
        // cobertura = currentStock * window / unitsSold; se compara sin dividir para que los límites sean exactos.
        long scaledStock = (long) currentStock * params.demandWindowDays();
        if (scaledStock < (long) params.highPriorityDays() * unitsSold) {
            return ReplenishmentPriority.ALTA;
        }
        if (scaledStock < (long) params.mediumPriorityDays() * unitsSold) {
            return ReplenishmentPriority.MEDIA;
        }
        return ReplenishmentPriority.BAJA;
    }

    private static long ceilDiv(long numerator, long denominator) {
        return (numerator + denominator - 1) / denominator;
    }
}
