package com.viptables.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.viptables.dto.ManagerDashboardResponse;
import com.viptables.dto.ReservationStatusRequest;
import com.viptables.dto.RestaurantRequest;
import com.viptables.dto.RestaurantTableRequest;
import com.viptables.exception.ResourceNotFoundException;
import com.viptables.exception.ReservationConflictException;
import com.viptables.model.Reservation;
import com.viptables.model.ReservationStatus;
import com.viptables.model.Restaurant;
import com.viptables.model.RestaurantTable;
import com.viptables.repository.ManagerDashboardRepository;
import com.viptables.repository.ReservationRepository;
import com.viptables.repository.RestaurantRepository;
import com.viptables.repository.RestaurantTableRepository;

@Service
public class ManagerService {

    private final ManagerDashboardRepository dashboardRepository;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantTableRepository tableRepository;
    private final ReservationRepository reservationRepository;

    public ManagerService(
            ManagerDashboardRepository dashboardRepository,
            RestaurantRepository restaurantRepository,
            RestaurantTableRepository tableRepository,
            ReservationRepository reservationRepository) {
        this.dashboardRepository = dashboardRepository;
        this.restaurantRepository = restaurantRepository;
        this.tableRepository = tableRepository;
        this.reservationRepository = reservationRepository;
    }

    public ManagerDashboardResponse getDashboard() {
        return dashboardRepository.getStatistics();
    }

    public List<Restaurant> findRestaurants() {
        return restaurantRepository.findAll();
    }

    public Restaurant createRestaurant(RestaurantRequest request) {
        return restaurantRepository.insert(
                request.name(), request.description(), request.address(),
                request.cuisine(), request.imageUrl());
    }

    public Restaurant updateRestaurant(long id, RestaurantRequest request) {
        requireRestaurant(id);
        Restaurant updated = restaurantRepository.update(
                id, request.name(), request.description(), request.address(),
                request.cuisine(), request.imageUrl());
        if (updated == null) {
            throw new ResourceNotFoundException("Restaurant " + id + " was not found");
        }
        return updated;
    }

    @Transactional
    public void deleteRestaurant(long id) {
        requireRestaurant(id);
        try {
            if (!restaurantRepository.deleteById(id)) {
                throw new ResourceNotFoundException("Restaurant " + id + " was not found");
            }
        } catch (org.springframework.dao.DataIntegrityViolationException exception) {
            throw new ReservationConflictException(
                    "Restaurant cannot be deleted while it has reservations; deactivate its tables instead");
        }
    }

    public List<RestaurantTable> findTables(long restaurantId) {
        requireRestaurant(restaurantId);
        return tableRepository.findAllByRestaurantId(restaurantId);
    }

    public RestaurantTable createTable(long restaurantId, RestaurantTableRequest request) {
        requireRestaurant(restaurantId);
        return tableRepository.insert(restaurantId, request.tableNumber(), request.capacity());
    }

    public RestaurantTable updateTable(long id, RestaurantTableRequest request) {
        requireTable(id);
        RestaurantTable updated = tableRepository.update(id, request.tableNumber(), request.capacity());
        if (updated == null) {
            throw new ResourceNotFoundException("Table " + id + " was not found");
        }
        return updated;
    }

    public RestaurantTable deactivateTable(long id) {
        RestaurantTable table = requireTable(id);
        if (!tableRepository.setActive(id, false)) {
            throw new ResourceNotFoundException("Table " + id + " was not found");
        }
        return tableRepository.findById(id).orElseThrow();
    }

    public List<Reservation> findReservations() {
        return reservationRepository.findAll();
    }

    public Reservation updateReservationStatus(long id, ReservationStatusRequest request) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reservation " + id + " was not found"));
        ReservationStatus status = request.status();
        if (status == ReservationStatus.CONFIRMED
                && reservationRepository.hasOverlappingConfirmedReservationExcept(
                        reservation.id(),
                        reservation.restaurantTableId(),
                        reservation.reservationDate(),
                        reservation.startTime(),
                        reservation.endTime())) {
            throw new ReservationConflictException(
                    "Reservation overlaps another confirmed reservation for the same table");
        }
        if (!reservationRepository.updateStatus(id, status)) {
            throw new IllegalStateException("Reservation status could not be updated");
        }
        return reservationRepository.findById(id).orElseThrow();
    }

    private Restaurant requireRestaurant(long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Restaurant " + id + " was not found"));
    }

    private RestaurantTable requireTable(long id) {
        return tableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Table " + id + " was not found"));
    }
}
