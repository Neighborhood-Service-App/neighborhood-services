package com.neighborhoodservice.user.dto;

import com.neighborhoodservice.user.model.Address;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UserResponse(
        UUID userId,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String about,
        List<Address> addresses,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String imgUrl
) {
}
