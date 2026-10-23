package com.viptables.model;

import java.time.Instant;

public record Restaurant(
        Long id,
        String name,
        String description,
        String address,
        String cuisine,
        String imageUrl,
        Instant createdAt) {
}
