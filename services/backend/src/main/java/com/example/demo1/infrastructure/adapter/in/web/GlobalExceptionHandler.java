package com.example.demo1.infrastructure.adapter.in.web;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.demo1.domain.InsufficientStockException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── Errores de validación Bean Validation (@Valid) ──────────────────────
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fe.getField(), fe.getDefaultMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status",    400,
                "error",     "Datos inválidos",
                "campos",    fieldErrors
        ));
    }

    // ── Stock insuficiente ──────────────────────────────────────────────────
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientStock(
            InsufficientStockException ex) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "timestamp",  LocalDateTime.now().toString(),
                "status",     409,
                "error",      "Stock insuficiente",
                "message",    ex.getMessage(),
                "productId",  ex.getProductId(),
                "disponible", ex.getDisponible(),
                "solicitado", ex.getSolicitado()
        ));
    }

    // ── Argumento ilegal (datos de negocio inválidos) ───────────────────────
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(
            IllegalArgumentException ex) {

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status",    422,
                "error",     "Datos de negocio inválidos",
                "message",   ex.getMessage()
        ));
    }

    // ── Recurso duplicado (ya existía) ──────────────────────────────────────
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(
            DuplicateResourceException ex) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status",    409,
                "error",     "Recurso duplicado",
                "message",   ex.getMessage()
        ));
    }

    // ── Fallback genérico ───────────────────────────────────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status",    500,
                "error",     "Error interno del servidor",
                "message",   ex.getMessage()
        ));
    }
}
