package com.neighborhoodservice.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.UUID;


public record RegisterRequest(

        @UUID
        @NotEmpty(message = "Invalid UUID format")
        String id,

        @NotEmpty
        @Email(message = "Invalid email format")
        String email
) {



}
