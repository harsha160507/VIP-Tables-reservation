package com.viptables.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.viptables.dto.ManagerDashboardResponse;
import com.viptables.dto.ReservationStatusRequest;
import com.viptables.dto.RestaurantRequest;
import com.viptables.dto.RestaurantTableRequest;
import com.viptables.model.Reservation;
import com.viptables.model.Restaurant;
import com.viptables.model.RestaurantTable;
import com.viptables.service.ManagerService;

@RestController
@RequestMapping("/api/manager")
public class ManagerController {

    private final ManagerService managerService;

    public ManagerController(ManagerService managerService) {
        this.managerService = managerService;
    }

    @GetMapping("/dashboard")
    public ManagerDashboardResponse dashboard() {
        return managerService.getDashboard();
    }

    @GetMapping("/restaurants")
    public List<Restaurant> restaurants() {
        return managerService.findRestaurants();
    }

    @PostMapping("/restaurants")
    @ResponseStatus(HttpStatus.CREATED)
    public Restaurant createRestaurant(@Valid @RequestBody RestaurantRequest request) {
        return managerService.createRestaurant(request);
    }

    @PutMapping("/restaurants/{id}")
    public Restaurant updateRestaurant(
            @PathVariable("id") long id,
            @Valid @RequestBody RestaurantRequest request) {
        return managerService.updateRestaurant(id, request);
    }

    @DeleteMapping("/restaurants/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRestaurant(@PathVariable("id") long id) {
        managerService.deleteRestaurant(id);
    }

    @GetMapping("/restaurants/{restaurantId}/tables")
    public List<RestaurantTable> tables(@PathVariable("restaurantId") long restaurantId) {
        return managerService.findTables(restaurantId);
    }

    @PostMapping("/restaurants/{restaurantId}/tables")
    @ResponseStatus(HttpStatus.CREATED)
    public RestaurantTable createTable(
            @PathVariable("restaurantId") long restaurantId,
            @Valid @RequestBody RestaurantTableRequest request) {
        return managerService.createTable(restaurantId, request);
    }

    @PutMapping("/tables/{id}")
    public RestaurantTable updateTable(
            @PathVariable("id") long id,
            @Valid @RequestBody RestaurantTableRequest request) {
        return managerService.updateTable(id, request);
    }

    @PatchMapping("/tables/{id}/deactivate")
    public RestaurantTable deactivateTable(@PathVariable("id") long id) {
        return managerService.deactivateTable(id);
    }

    @GetMapping("/reservations")
    public List<Reservation> reservations() {
        return managerService.findReservations();
    }

    @PatchMapping("/reservations/{id}/status")
    public Reservation updateReservationStatus(
            @PathVariable("id") long id,
            @Valid @RequestBody ReservationStatusRequest request) {
        return managerService.updateReservationStatus(id, request);
    }
}
