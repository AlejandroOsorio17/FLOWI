package com.example.demo1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.example.demo1.domain.replenishment.ReplenishmentCalculator;
import com.example.demo1.domain.replenishment.ReplenishmentParams;
import com.example.demo1.domain.replenishment.ReplenishmentPriority;
import com.example.demo1.domain.replenishment.ReplenishmentResult;

class ReplenishmentCalculatorTest {

    private static final double DELTA = 1e-9;

    // Params: ventana 30 días, cobertura 7 días, ALTA < 3 días, MEDIA < 7 días.
    private final ReplenishmentCalculator calculator =
            new ReplenishmentCalculator(new ReplenishmentParams(30, 7, 3, 7));

    @Test
    void shouldCalculateDeficitAndHighPriorityForFastSellingProduct() {
        // Arrange: 300 vendidas en 30 días => 10/día; target = max(10, 70) = 70
        // Act
        ReplenishmentResult result = calculator.calculate(20, 10, 300, null);

        // Assert
        assertEquals(10.0, result.dailyDemand(), DELTA);
        assertEquals(50, result.deficit());
        assertEquals(50, result.recommendedQuantity());
        assertEquals(2.0, result.coverageDays(), DELTA);
        assertEquals(ReplenishmentPriority.ALTA, result.priority());
        assertTrue(result.requiresReplenishment());
    }

    @Test
    void shouldNotRequireReplenishmentWhenStockEqualsTarget() {
        // Arrange: target = 70, stock = 70
        // Act
        ReplenishmentResult result = calculator.calculate(70, 10, 300, null);

        // Assert
        assertEquals(0, result.deficit());
        assertEquals(0, result.recommendedQuantity());
        assertFalse(result.requiresReplenishment());
        assertNull(result.priority());
    }

    @Test
    void shouldReportDeficitOfOneWhenStockIsTargetMinusOne() {
        // Arrange: target = 70, stock = 69
        // Act
        ReplenishmentResult result = calculator.calculate(69, 10, 300, null);

        // Assert
        assertEquals(1, result.deficit());
        assertEquals(1, result.recommendedQuantity());
        assertTrue(result.requiresReplenishment());
    }

    @Test
    void shouldUseMinimumAsTargetAndLowPriorityWhenNoDemandAndStockBelowMinimum() {
        // Arrange: sin ventas => target = mínimo = 10
        // Act
        ReplenishmentResult result = calculator.calculate(5, 10, 0, null);

        // Assert
        assertEquals(0.0, result.dailyDemand(), DELTA);
        assertNull(result.coverageDays());
        assertEquals(5, result.deficit());
        assertEquals(ReplenishmentPriority.BAJA, result.priority());
    }

    @Test
    void shouldAssignHighPriorityWhenNoDemandAndStockIsZero() {
        // Arrange: sin ventas y sin stock
        // Act
        ReplenishmentResult result = calculator.calculate(0, 10, 0, null);

        // Assert
        assertEquals(10, result.deficit());
        assertNull(result.coverageDays());
        assertEquals(ReplenishmentPriority.ALTA, result.priority());
    }

    @Test
    void shouldUseProjectedDemandWhenItExceedsMinimum() {
        // Arrange: demanda proyectada 70 > mínimo 10 => target 70
        // Act
        ReplenishmentResult result = calculator.calculate(10, 10, 300, null);

        // Assert
        assertEquals(60, result.deficit());
    }

    @Test
    void shouldUseMinimumWhenItExceedsProjectedDemand() {
        // Arrange: 30 vendidas => 1/día => proyectada 7 < mínimo 50 => target 50
        // Act
        ReplenishmentResult result = calculator.calculate(10, 50, 30, null);

        // Assert
        assertEquals(40, result.deficit());
    }

    @Test
    void shouldRoundProjectedDemandUp() {
        // Arrange: 10 vendidas => 0.333/día => 0.333 * 7 = 2.33 => target 3
        // Act
        ReplenishmentResult result = calculator.calculate(0, 0, 10, null);

        // Assert
        assertEquals(3, result.deficit());
    }

    @Test
    void shouldAssignHighPriorityWhenCoverageIsBelowThreeDays() {
        // Arrange: 10/día, mínimo 100 => target 100; stock 29 => cobertura 2.9
        // Act
        ReplenishmentResult result = calculator.calculate(29, 100, 300, null);

        // Assert
        assertEquals(2.9, result.coverageDays(), 1e-6);
        assertEquals(ReplenishmentPriority.ALTA, result.priority());
    }

    @Test
    void shouldAssignMediumPriorityWhenCoverageIsExactlyThreeDays() {
        // Arrange: stock 30 => cobertura 3.0
        // Act
        ReplenishmentResult result = calculator.calculate(30, 100, 300, null);

        // Assert
        assertEquals(ReplenishmentPriority.MEDIA, result.priority());
    }

    @Test
    void shouldAssignMediumPriorityWhenCoverageIsBelowSevenDays() {
        // Arrange: stock 69 => cobertura 6.9
        // Act
        ReplenishmentResult result = calculator.calculate(69, 100, 300, null);

        // Assert
        assertEquals(ReplenishmentPriority.MEDIA, result.priority());
    }

    @Test
    void shouldAssignLowPriorityWhenCoverageIsExactlySevenDays() {
        // Arrange: stock 70 => cobertura 7.0, target 100 => hay déficit de 30
        // Act
        ReplenishmentResult result = calculator.calculate(70, 100, 300, null);

        // Assert
        assertEquals(30, result.deficit());
        assertEquals(ReplenishmentPriority.BAJA, result.priority());
    }

    @Test
    void shouldLimitRecommendedQuantityToCenterStock() {
        // Arrange: déficit 50, centro con 30
        // Act
        ReplenishmentResult result = calculator.calculate(20, 10, 300, 30);

        // Assert
        assertEquals(50, result.deficit());
        assertEquals(30, result.recommendedQuantity());
    }

    @Test
    void shouldRecommendFullDeficitWhenCenterHasEnoughStock() {
        // Arrange: déficit 50, centro con 100
        // Act
        ReplenishmentResult result = calculator.calculate(20, 10, 300, 100);

        // Assert
        assertEquals(50, result.recommendedQuantity());
    }

    @Test
    void shouldRecommendZeroButKeepDeficitWhenCenterStockIsZero() {
        // Arrange: déficit 50, centro sin stock
        // Act
        ReplenishmentResult result = calculator.calculate(20, 10, 300, 0);

        // Assert
        assertEquals(50, result.deficit());
        assertEquals(0, result.recommendedQuantity());
        assertTrue(result.requiresReplenishment());
    }

    @Test
    void shouldRecommendDeficitWhenCenterStockIsNull() {
        // Arrange: stock del centro desconocido
        // Act
        ReplenishmentResult result = calculator.calculate(20, 10, 300, null);

        // Assert
        assertEquals(result.deficit(), result.recommendedQuantity());
    }

    @Test
    void shouldRejectNegativeInputs() {
        // Arrange: valores negativos en cada argumento
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> calculator.calculate(-1, 10, 100, null));
        assertThrows(IllegalArgumentException.class, () -> calculator.calculate(10, -1, 100, null));
        assertThrows(IllegalArgumentException.class, () -> calculator.calculate(10, 10, -1, null));
    }

    @Test
    void shouldRejectInvalidParams() {
        // Arrange: parámetros con cero, negativos o highPriorityDays >= mediumPriorityDays
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new ReplenishmentParams(0, 7, 3, 7));
        assertThrows(IllegalArgumentException.class, () -> new ReplenishmentParams(30, 0, 3, 7));
        assertThrows(IllegalArgumentException.class, () -> new ReplenishmentParams(30, 7, 0, 7));
        assertThrows(IllegalArgumentException.class, () -> new ReplenishmentParams(30, 7, 3, 0));
        assertThrows(IllegalArgumentException.class, () -> new ReplenishmentParams(-30, 7, 3, 7));
        assertThrows(IllegalArgumentException.class, () -> new ReplenishmentParams(30, 7, 7, 7));
        assertThrows(IllegalArgumentException.class, () -> new ReplenishmentParams(30, 7, 8, 7));
    }
}
