package com.neighborhoodservice.user.service;

import com.neighborhoodservice.user.dto.AddressRequest;
import com.neighborhoodservice.user.dto.AddressResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface AddressService {

    ResponseEntity<AddressResponse> addAddress(UUID userId, AddressRequest addressRequest) throws Exception;

    List<AddressResponse> getAllAddresses(UUID userId);

    ResponseEntity<HttpStatus> deleteAddressById(UUID userId, Long addressId);

    ResponseEntity<HttpStatus> updateAddress(
            UUID userId,
            Long addressId,
            AddressRequest addressRequest);

    AddressResponse getAddressById(UUID userId, Long addressId);
}
