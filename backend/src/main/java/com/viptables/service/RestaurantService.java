package com.viptables.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.viptables.exception.ResourceNotFoundException;
import com.viptables.model.Restaurant;
import com.viptables.repository.RestaurantRepository;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public List<Restaurant> findAll() {
        return restaurantRepository.findAll();
    }

    public Restaurant findById(long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant " + id + " was not found"));
    }
}
