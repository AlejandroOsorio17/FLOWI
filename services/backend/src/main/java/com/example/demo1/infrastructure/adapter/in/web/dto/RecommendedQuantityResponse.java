package com.example.demo1.infrastructure.adapter.in.web.dto;

import com.example.demo1.domain.replenishment.ReplenishmentPriority;

public record RecommendedQuantityResponse(Long storeId,
                                          Long productId,
                                          String productCode,
                                          String productName,
                                          int currentStock,
                                          int minimumStock,
                                          double dailyDemand,
                                          int deficit,
                                          int recommendedQuantity,
                                          Long originCenterId,
                                          String originCenterName,
                                          Integer originCenterStock,
                                          ReplenishmentPriority priority) {
}
