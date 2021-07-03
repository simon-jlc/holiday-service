package org.holiday.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(path = "/api/health")
public class HealthCheckController {

    @GetMapping
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "service", "holiday-service",
                "status", "green (I say green !)",
                "verions", "v1.0.0"
        ));
    }
}
