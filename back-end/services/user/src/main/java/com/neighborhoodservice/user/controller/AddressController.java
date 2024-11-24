package com.neighborhoodservice.user.controller;

import com.neighborhoodservice.user.authorizationUtils.JWTUtils;
import com.neighborhoodservice.user.dto.AddressRequest;
import com.neighborhoodservice.user.dto.AddressResponse;
import com.neighborhoodservice.user.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/addresses")
@RequiredArgsConstructor
public class AddressController {

    private static final Logger log = LoggerFactory.getLogger(AddressController.class);
    private final AddressService addressService;
    private final JWTUtils JWTUtils;

    @PostMapping
    public ResponseEntity<HttpStatus> addAddress(
            @RequestBody @Valid AddressRequest addressRequest,
            @RequestHeader("Authorization") String token
    ) throws Exception {

        UUID userId = JWTUtils.getUserIdFromToken(token);
        ResponseEntity<HttpStatus> result = addressService.addAddress(userId, addressRequest, token);

        log.info("New address added to user with id {}", userId);
        return result;
    }
    
    @GetMapping
    public ResponseEntity<List<AddressResponse>> getAllAddresses(
            @RequestHeader("Authorization") String token
    ) throws Exception {

        UUID userId = JWTUtils.getUserIdFromToken(token);
        log.info("Getting all addresses for user with id {}", userId);
        return ResponseEntity.ok(addressService.getAllAddresses(userId, token));

    }


    @DeleteMapping("/{addressId}")
    public ResponseEntity<HttpStatus> deleteAddress(
            @PathVariable Long addressId,
            @RequestHeader("Authorization") String token
    ) throws Exception {

        UUID userId = JWTUtils.getUserIdFromToken(token);
        return addressService.deleteAddressById(userId, addressId, token);

    }

//    TODO: Implement update address endpoint
    @PatchMapping("/{addressId}")
    public ResponseEntity<HttpStatus> updateAddress(
            @PathVariable Long addressId,
            @RequestBody @Valid AddressRequest addressRequest,
            @RequestHeader("Authorization") String token
    ) throws Exception {

        UUID userId = JWTUtils.getUserIdFromToken(token);
        return addressService.updateAddress(userId, addressId, addressRequest, token);

    }


}
