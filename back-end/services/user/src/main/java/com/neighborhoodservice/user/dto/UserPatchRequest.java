package com.neighborhoodservice.user.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record UserPatchRequest(
        @Length(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
        String firstName,

        @Length(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
        String lastName,

        @Column(length = 13)
        @Pattern(regexp = "^\\+?3?8?(0\\d{9})$", message = "Invalid phone number format")
        String phoneNumber,

        @Length(min = 1, max = 255, message = "Last name must be between 1 and 100 characters")
        String about

) {
}
