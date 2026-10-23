package com.viptables.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

import com.viptables.model.ReservationStatus;

public record ReservationResponse(
        Long id,
        Long customerId,
        Long restaurantTableId,
        LocalDate reservationDate,
        LocalTime startTime,
        LocalTime endTime,
        ReservationStatus status,
        Instant createdAt) {
}
