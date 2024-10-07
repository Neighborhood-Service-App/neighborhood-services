package com.neighborhoodservice.user;

public record LoginRequest(
    String email,
    String password
) {
}
