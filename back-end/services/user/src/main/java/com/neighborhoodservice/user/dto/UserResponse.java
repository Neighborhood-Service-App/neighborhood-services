package com.neighborhoodservice.user.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(

        UUID userId,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String about,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String imgUrl
) {
    public UserResponse(UserResponse userResponse, String imgUrl) {
        this(userResponse.userId(),
                userResponse.firstName(),
                userResponse.lastName(),
                userResponse.email(),
                userResponse.phoneNumber(),
                userResponse.about(),
                userResponse.createdAt(),
                userResponse.updatedAt(),
                imgUrl);
    }
}
