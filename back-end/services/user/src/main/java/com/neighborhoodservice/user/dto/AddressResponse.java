package com.neighborhoodservice.user.dto;

public record AddressResponse(
        String address,
        String city,
        String postalCode,
        String addressType,
        boolean isDefault
) {
}
