package com.neighborhoodservice.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.UUID;


public record RegisterRequest(

        @NotEmpty(message = "First name cannot be empty")
        String firstName,

        @NotEmpty(message = "Last name cannot be empty")
        String lastName,

        @NotEmpty(message = "Password cannot be empty")
        String password,

        @NotEmpty
        @Email(message = "Invalid email format")
        String email
) {



}
