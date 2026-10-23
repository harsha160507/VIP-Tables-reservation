package com.viptables.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

public record Reservation(
        Long id,
        Long customerId,
        Long restaurantTableId,
        LocalDate reservationDate,
        LocalTime startTime,
        LocalTime endTime,
        ReservationStatus status,
        Instant createdAt) {
}
