package com.viptables.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.viptables.dto.AvailableTableResponse;
import com.viptables.exception.ResourceNotFoundException;
import com.viptables.model.RestaurantTable;
import com.viptables.repository.ReservationRepository;
import com.viptables.repository.RestaurantRepository;
import com.viptables.repository.RestaurantTableRepository;

@Service
public class AvailabilityService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantTableRepository tableRepository;
    private final ReservationRepository reservationRepository;

    public AvailabilityService(
            RestaurantRepository restaurantRepository,
            RestaurantTableRepository tableRepository,
            ReservationRepository reservationRepository) {
        this.restaurantRepository = restaurantRepository;
        this.tableRepository = tableRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<AvailableTableResponse> findAvailableTables(
            long restaurantId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime) {
        restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Restaurant " + restaurantId + " was not found"));

        validateTimeWindow(date, startTime, endTime);
        return tableRepository.findActiveByRestaurantId(restaurantId).stream()
                .map(table -> new AvailableTableResponse(
                        table,
                        !reservationRepository.hasOverlappingConfirmedReservation(
                                table.id(), date, startTime, endTime)))
                .filter(AvailableTableResponse::available)
                .toList();
    }

    private void validateTimeWindow(
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime) {
        if (date == null || startTime == null || endTime == null) {
            throw new IllegalArgumentException("date, startTime and endTime are required");
        }
        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Reservation date cannot be in the past");
        }
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("endTime must be after startTime");
        }
    }
}
