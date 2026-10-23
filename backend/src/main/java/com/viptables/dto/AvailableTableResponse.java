package com.viptables.dto;

import com.viptables.model.RestaurantTable;

public record AvailableTableResponse(
        RestaurantTable table,
        boolean available) {
}
