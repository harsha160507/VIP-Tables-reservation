package com.viptables.dto;

import java.time.Instant;

import com.viptables.model.UserRole;

public record UserResponse(
        Long id,
        String fullName,
        String email,
        UserRole role,
        Instant createdAt) {
}
