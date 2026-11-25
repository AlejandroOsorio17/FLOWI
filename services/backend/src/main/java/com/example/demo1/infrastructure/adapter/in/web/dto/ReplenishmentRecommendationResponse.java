package com.example.demo1.infrastructure.adapter.in.web.dto;

import com.example.demo1.domain.replenishment.ReplenishmentPriority;

public record ReplenishmentRecommendationResponse(Long storeId,
                                                  String storeName,
                                                  Long productId,
                                                  String productCode,
                                                  String productName,
                                                  Long originCenterId,
                                                  String originCenterName,
                                                  int currentStock,
                                                  int deficit,
                                                  int recommendedQuantity,
                                                  ReplenishmentPriority priority,
                                                  double dailyDemand) {
}
