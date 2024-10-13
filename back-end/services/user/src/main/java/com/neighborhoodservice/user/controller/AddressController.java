package com.neighborhoodservice.user.controller;

import com.neighborhoodservice.user.authorizationUtils.JWTUtils;
import com.neighborhoodservice.user.dto.AddressPatchRequest;
import com.neighborhoodservice.user.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        if (!JWTUtils.getUserIdFromToken(token).equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .build();
        }

        addressService.addAddress(userId, addressPatchRequest);
        log.info("New address added to user with id {}", userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

}
