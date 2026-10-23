package com.viptables.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import com.viptables.dto.CreateReservationRequest;
import com.viptables.dto.ReservationResponse;
import com.viptables.exception.ReservationConflictException;
import com.viptables.exception.ResourceNotFoundException;
import com.viptables.model.Reservation;
import com.viptables.model.ReservationStatus;
import com.viptables.model.RestaurantTable;
import com.viptables.model.User;
import com.viptables.model.UserRole;
import com.viptables.repository.ReservationRepository;
import com.viptables.repository.RestaurantTableRepository;
import com.viptables.repository.UserRepository;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RestaurantTableRepository tableRepository;
    private final UserRepository userRepository;

    public ReservationService(
            ReservationRepository reservationRepository,
            RestaurantTableRepository tableRepository,
            UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.tableRepository = tableRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ReservationResponse create(CreateReservationRequest request, String authenticatedEmail) {
        requireCustomerOwnership(request.customerId(), authenticatedEmail);
        validateTimeWindow(
                request.reservationDate(),
                request.startTime(),
                request.endTime());

        RestaurantTable table = tableRepository.findActiveByIdForUpdate(request.restaurantTableId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Active table " + request.restaurantTableId() + " was not found"));

        if (reservationRepository.hasOverlappingConfirmedReservation(
                table.id(),
                request.reservationDate(),
                request.startTime(),
                request.endTime())) {
            throw new ReservationConflictException(
                    "Table " + table.tableNumber() + " is already reserved for that time");
        }

        return toResponse(reservationRepository.insert(
                request.customerId(),
                table.id(),
                request.reservationDate(),
                request.startTime(),
                request.endTime()));
    }

    public List<ReservationResponse> findByCustomerId(long customerId, String authenticatedEmail) {
        requireCustomerOwnership(customerId, authenticatedEmail);
        return reservationRepository.findByCustomerId(customerId).stream()
                .map(this::toResponse)
                .toList();
    }

    public ReservationResponse findById(long id, String authenticatedEmail) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reservation " + id + " was not found"));
        requireReservationAccess(reservation, authenticatedEmail);
        return toResponse(reservation);
    }

    @Transactional
    public ReservationResponse cancel(long id, long customerId, String authenticatedEmail) {
        requireCustomerOwnership(customerId, authenticatedEmail);
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reservation " + id + " was not found"));
        if (reservation.customerId() != customerId) {
            throw new ReservationConflictException(
                    "You can only cancel your own reservations");
        }
        if (reservation.status() == ReservationStatus.CANCELLED) {
            return toResponse(reservation);
        }
        if (!reservationRepository.cancel(id)) {
            throw new ReservationConflictException(
                    "Reservation could not be cancelled");
        }
        return findById(id, authenticatedEmail);
    }

    private void validateTimeWindow(LocalDate date, LocalTime startTime, LocalTime endTime) {
        if (date == null || startTime == null || endTime == null) {
            throw new IllegalArgumentException("reservationDate, startTime and endTime are required");
        }

        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Reservation date cannot be in the past");
        }
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("endTime must be after startTime");
        }
    }

    private void requireCustomerOwnership(long customerId, String authenticatedEmail) {
        User user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user was not found"));
        if (user.role() != UserRole.CUSTOMER || user.id() != customerId) {
            throw new AccessDeniedException(
                    "Customers may only access their own reservations");
        }
    }

    private void requireReservationAccess(Reservation reservation, String authenticatedEmail) {
        User user = userRepository.findByEmail(authenticatedEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("Authenticated user was not found"));
        if (user.role() != UserRole.MANAGER
                    && (user.role() != UserRole.CUSTOMER || user.id() != reservation.customerId())) {
            throw new AccessDeniedException("You can only access your own reservations");
        }
    }

    private ReservationResponse toResponse(Reservation reservation) {
        return new ReservationResponse(
                reservation.id(),
                reservation.customerId(),
                reservation.restaurantTableId(),
                reservation.reservationDate(),
                reservation.startTime(),
                reservation.endTime(),
                reservation.status(),
                reservation.createdAt());
    }
}
