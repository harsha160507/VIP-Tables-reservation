package com.viptables.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record RestaurantTableRequest(
        @NotBlank @Size(max = 20) String tableNumber,
        @Positive int capacity) {
}
