package com.neighborhoodservice.user.dto;

import com.neighborhoodservice.user.model.Address;
import com.neighborhoodservice.user.validation.ValidPhoneNumber;
import jakarta.persistence.Column;
import jakarta.validation.Valid;
import org.hibernate.validator.constraints.Length;

import java.util.List;

public record UserPatchRequest(
        @Length(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
        String firstName,

        @Length(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
        String lastName,

        @Column(length = 13)
        @ValidPhoneNumber
        String phoneNumber,

        @Length(min = 1, max = 255, message = "Last name must be between 1 and 100 characters")
        String about,

//        @Valid
        List<Address> addresses,

//        For now, the image upload functionality is just a String
        String imgUrl
) {
}
