package com.viptables.controller;

import java.util.List;
import java.security.Principal;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.viptables.dto.CreateReservationRequest;
import com.viptables.dto.ReservationResponse;
import com.viptables.service.ReservationService;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse create(
            @Valid @RequestBody CreateReservationRequest request,
            Principal principal) {
        return reservationService.create(request, principal.getName());
    }

    @GetMapping
    public List<ReservationResponse> findByCustomer(
            @RequestParam("customerId") long customerId,
            Principal principal) {
        return reservationService.findByCustomerId(customerId, principal.getName());
    }

    @GetMapping("/{id}")
    public ReservationResponse findById(
            @PathVariable("id") long id,
            Principal principal) {
        return reservationService.findById(id, principal.getName());
    }

    @PatchMapping("/{id}/cancel")
    public ReservationResponse cancel(
            @PathVariable("id") long id,
            @RequestParam("customerId") long customerId,
            Principal principal) {
        return reservationService.cancel(id, customerId, principal.getName());
    }
}
