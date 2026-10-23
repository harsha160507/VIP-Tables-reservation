package com.viptables.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RestaurantRequest(
        @NotBlank @Size(max = 150) String name,
        @Size(max = 2000) String description,
        @NotBlank @Size(max = 255) String address,
        @NotBlank @Size(max = 80) String cuisine,
        @Size(max = 500) String imageUrl) {
}
