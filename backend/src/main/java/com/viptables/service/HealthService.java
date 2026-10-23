package com.viptables.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.viptables.dto.HealthResponse;
import com.viptables.repository.DatabaseHealthRepository;

@Service
public class HealthService {

    private final DatabaseHealthRepository databaseHealthRepository;

    public HealthService(DatabaseHealthRepository databaseHealthRepository) {
        this.databaseHealthRepository = databaseHealthRepository;
    }

    public HealthResponse getHealth() {
        boolean databaseAvailable = databaseHealthRepository.isReachable();
        return new HealthResponse(
                databaseAvailable ? "UP" : "DOWN",
                databaseAvailable ? "UP" : "DOWN",
                Instant.now());
    }
}
