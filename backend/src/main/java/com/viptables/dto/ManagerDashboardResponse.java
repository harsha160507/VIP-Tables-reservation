package com.viptables.dto;

public record ManagerDashboardResponse(
        long restaurantCount,
        long activeTableCount,
        long confirmedReservationCount,
        long cancelledReservationCount) {
}
