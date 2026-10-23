package com.viptables.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.viptables.dto.RegisterRequest;
import com.viptables.dto.UserResponse;
import com.viptables.exception.ResourceNotFoundException;
import com.viptables.model.User;
import com.viptables.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("An account with that email already exists");
        }
        return toResponse(userRepository.insertCustomer(
                request.fullName(),
                request.email(),
                passwordEncoder.encode(request.password())));
    }

    public UserResponse findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user was not found"));
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.id(), user.fullName(), user.email(), user.role(), user.createdAt());
    }
}
