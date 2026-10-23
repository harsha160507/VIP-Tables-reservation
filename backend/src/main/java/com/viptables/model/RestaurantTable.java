package com.viptables.model;

public record RestaurantTable(
        Long id,
        Long restaurantId,
        String tableNumber,
        int capacity,
        boolean active) {
}
