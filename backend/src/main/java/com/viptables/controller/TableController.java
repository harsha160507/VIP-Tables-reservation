package com.viptables.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.viptables.dto.AvailableTableResponse;
import com.viptables.model.RestaurantTable;
import com.viptables.service.AvailabilityService;
import com.viptables.service.TableService;

@RestController
@RequestMapping("/api")
public class TableController {

    private final TableService tableService;
    private final AvailabilityService availabilityService;

    public TableController(
            TableService tableService,
            AvailabilityService availabilityService) {
        this.tableService = tableService;
        this.availabilityService = availabilityService;
    }

    @GetMapping("/restaurants/{restaurantId}/tables")
    public List<RestaurantTable> findTables(@PathVariable("restaurantId") long restaurantId) {
        return tableService.findActiveByRestaurantId(restaurantId);
    }

    @GetMapping("/restaurants/{restaurantId}/availability")
    public List<AvailableTableResponse> findAvailability(
            @PathVariable("restaurantId") long restaurantId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("startTime") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam("endTime") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime) {
        return availabilityService.findAvailableTables(
                restaurantId,
                date,
                startTime,
                endTime);
    }
}
