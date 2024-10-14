package com.neighborhoodservice.user.dto;

public record AddressResponse(
        Long addressId,
        String address,
        String city,
        String postalCode,
        String addressType,
        boolean isDefault
) {
}
