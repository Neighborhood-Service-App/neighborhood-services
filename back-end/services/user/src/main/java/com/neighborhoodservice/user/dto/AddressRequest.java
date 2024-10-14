package com.neighborhoodservice.user.dto;

import com.neighborhoodservice.user.model.AddressType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

public record AddressRequest(

        @NotEmpty(message = "City cannot be empty")
        @Length(min = 1, max = 100, message = "City must be between 1 and 100 characters")
        String city,

        @NotEmpty
        @Size(min = 5, max = 5, message = "Postal code must be exactly 5 digits")
        String postalCode,

        @NotEmpty(message = "Address cannot be empty")
        String address,

        @NotNull(message = "Address type cannot be empty")
        @Enumerated(EnumType.STRING)
        AddressType addressType,  // Treating ENUM as String

        boolean isDefault
) {
}
