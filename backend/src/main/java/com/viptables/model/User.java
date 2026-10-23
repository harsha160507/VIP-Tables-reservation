package com.viptables.model;

import java.time.Instant;

public record User(
        Long id,
        String fullName,
        String email,
        String passwordHash,
        UserRole role,
        Instant createdAt) {
}
