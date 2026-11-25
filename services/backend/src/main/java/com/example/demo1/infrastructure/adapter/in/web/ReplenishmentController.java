package com.example.demo1.infrastructure.adapter.in.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo1.application.service.ReplenishmentService;
import com.example.demo1.infrastructure.adapter.in.web.dto.RecommendedQuantityResponse;
import com.example.demo1.infrastructure.adapter.in.web.dto.ReplenishmentRecommendationResponse;
import com.example.demo1.infrastructure.adapter.in.web.dto.RequiredProductResponse;

@RestController
@RequestMapping("/api/reposiciones")
public class ReplenishmentController {

    private final ReplenishmentService service;

    public ReplenishmentController(ReplenishmentService service) {
        this.service = service;
    }

    @GetMapping("/productos-requeridos")
    public List<RequiredProductResponse> getRequiredProducts(@RequestParam Long storeId) {
        return service.findRequiredProducts(storeId);
    }

    @GetMapping("/cantidad-recomendada/{productoId}")
    public RecommendedQuantityResponse getRecommendedQuantity(@PathVariable("productoId") Long productId,
                                                              @RequestParam Long storeId) {
        return service.findRecommendedQuantity(storeId, productId);
    }

    @GetMapping("/recomendaciones")
    public List<ReplenishmentRecommendationResponse> getRecommendations(@RequestParam Long storeId) {
        return service.findRecommendations(storeId);
    }
}
