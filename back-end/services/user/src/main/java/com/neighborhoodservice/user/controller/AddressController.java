package com.neighborhoodservice.user.controller;

import com.neighborhoodservice.user.authorizationUtils.JWTUtils;
import com.neighborhoodservice.user.dto.AddressPatchRequest;
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
@RequestMapping("/api/v1/users/{userId}/addresses")
@RequiredArgsConstructor
public class AddressController {

    private static final Logger log = LoggerFactory.getLogger(AddressController.class);
    private final AddressService addressService;
    private final JWTUtils JWTUtils;

    @PostMapping
    public ResponseEntity<HttpStatus> addAddress(
            @PathVariable UUID userId,
            @RequestBody @Valid AddressPatchRequest addressPatchRequest,
            @RequestHeader("Authorization") String token
    ) throws Exception {

//        Authorization check
        if (!authorizeUser(userId, token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .build();
        }

        ResponseEntity<HttpStatus> result = addressService.addAddress(userId, addressPatchRequest);

        log.info("New address added to user with id {}", userId);
        return result;
    }
    
    @GetMapping
    public ResponseEntity<List<AddressResponse>> getAllAddresses(
            @PathVariable UUID userId,
            @RequestHeader("Authorization") String token
    ) throws Exception {

//        Authorization check
        if (!authorizeUser(userId, token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .build();
        }

        log.info("Getting all addresses for user with id {}", userId);
        return ResponseEntity.ok(addressService.getAllAddresses(userId));

    }


    @DeleteMapping("/{addressId}")
    public ResponseEntity<HttpStatus> deleteAddress(
            @PathVariable UUID userId,
            @PathVariable Long addressId,
            @RequestHeader("Authorization") String token
    ) throws Exception {

        return addressService.deleteAddressById(userId, addressId, token);

    }

//    TODO: Implement update address endpoint


    /**
     * Authorize user by checking if the token belongs to the user
     * @param userId user id
     * @param token JWT token
     * @return true if the token belongs to the user
     * @throws Exception if the token is invalid
     */
    private boolean authorizeUser(UUID userId, String token) throws Exception {
        if (JWTUtils.getUserIdFromToken(token).equals(userId)) {
            return true;
        }
        return false;
    }


}
