package com.neighborhoodservice.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RegisterRequest(

        @NotNull(message = "Id cannot be blank")
        UUID id,

        @Email(message = "Email should be valid")
        String email
) {



}
