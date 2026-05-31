package com.quizvault.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/")
    public ResponseEntity<Map<String, String>> rootHealth() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "message", "QuizVault Backend API is running successfully on Railway!",
            "version", "1.0.0"
        ));
    }

    @GetMapping("/api/health")
    public ResponseEntity<Map<String, String>> apiHealth() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "message", "QuizVault Backend API is healthy",
            "version", "1.0.0"
        ));
    }
}
