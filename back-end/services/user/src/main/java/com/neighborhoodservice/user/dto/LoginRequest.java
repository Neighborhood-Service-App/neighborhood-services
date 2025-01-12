package com.neighborhoodservice.user.dto;

public record LoginRequest(
        String email,
        String password
) {
}
