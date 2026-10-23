package com.viptables.dto;

import com.viptables.model.ReservationStatus;

import jakarta.validation.constraints.NotNull;

public record ReservationStatusRequest(@NotNull ReservationStatus status) {
}
