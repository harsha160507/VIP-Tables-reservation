package com.viptables.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.viptables.exception.ResourceNotFoundException;
import com.viptables.model.RestaurantTable;
import com.viptables.repository.RestaurantRepository;
import com.viptables.repository.RestaurantTableRepository;

@Service
public class TableService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantTableRepository tableRepository;

    public TableService(
            RestaurantRepository restaurantRepository,
            RestaurantTableRepository tableRepository) {
        this.restaurantRepository = restaurantRepository;
        this.tableRepository = tableRepository;
    }

    public List<RestaurantTable> findActiveByRestaurantId(long restaurantId) {
        restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Restaurant " + restaurantId + " was not found"));
        return tableRepository.findActiveByRestaurantId(restaurantId);
    }
}
