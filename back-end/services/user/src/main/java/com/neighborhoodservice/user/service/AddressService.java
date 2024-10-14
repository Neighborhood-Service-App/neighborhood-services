package com.neighborhoodservice.user.service;

import com.neighborhoodservice.user.authorizationUtils.JWTUtils;
import com.neighborhoodservice.user.dto.AddressPatchRequest;
import com.neighborhoodservice.user.dto.AddressResponse;
import com.neighborhoodservice.user.exception.ResourceAlreadyExistsException;
import com.neighborhoodservice.user.exception.ResourceNotFoundException;
import com.neighborhoodservice.user.model.Address;
import com.neighborhoodservice.user.model.User;
import com.neighborhoodservice.user.repository.AddressRepository;
import com.neighborhoodservice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AddressService {

    private static final Logger log = LoggerFactory.getLogger(AddressService.class);
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final JWTUtils JWTUtils;

    public ResponseEntity<HttpStatus> addAddress(UUID userId, AddressPatchRequest addressPatchRequest) {

//        Check if user exists
        User user = userRepository.findById(userId)
                .orElseThrow( () -> new ResourceNotFoundException("User with id " + userId + " not found"));

//        Add logic for checking if user has an address with the same address type or already 3 addresses
        List<Address> addresses = addressRepository.findAllByUser(user);
        if (addresses.size() >= 3) {
            throw new ResourceAlreadyExistsException("User with id " + userId + " already has 3 addresses");
        }
        for (Address address : addresses) {
            if (address.getAddressType().equals(addressPatchRequest.addressType())) {
                throw new ResourceAlreadyExistsException("User with id " + userId + " already has an address with type " + addressPatchRequest.addressType());
            }
        }

        // Add address to the user
        Address address = new Address(
                user,
                addressPatchRequest.address(),
                addressPatchRequest.city(),
                addressPatchRequest.postalCode(),
//                TODO: Add logic for computing latitude and longitude(Google Maps API or OpenStreetMap)
                new BigDecimal("0.0"),
                new BigDecimal("0.0"),
                addressPatchRequest.addressType(),
                addressPatchRequest.isDefault()
        );

//        Save address to the database
        addressRepository.save(address);
        return ResponseEntity.accepted()
                .build();
    }


    public List<AddressResponse> getAllAddresses(UUID userId) {

            User user = userRepository.findById(userId)
                    .orElseThrow( () -> new ResourceNotFoundException("User with id " + userId + " not found"));

            List<Address> addresses = addressRepository.findAllByUser(user);

            return addresses.stream()
                    .map(address -> new AddressResponse(
                            address.getAddressId(),
                            address.getAddress(),
                            address.getCity(),
                            address.getPostalCode(),
                            address.getAddressType().toString(),
                            address.isDefault()
                    ))
                    .toList();
    }

    public ResponseEntity<HttpStatus> deleteAddressById(UUID userId, Long addressId, String token) throws Exception {

//            Check if user exists
            if (!userRepository.existsById(userId)) {
                throw new ResourceNotFoundException("User with id " + userId + " not found");
            }

//            Check if address exists and if so, store in variable
           Address address = addressRepository.findById(addressId)
                   .orElseThrow( () -> new ResourceNotFoundException("Address with id " + addressId + " not found"));

//            Check if address belongs to the user
            if (!address.getUser().getUserId().equals(userId)) {
                throw new ResourceNotFoundException("Address with id " + addressId + " is not found for user with id " + userId);
            }

//            Check if user is authorized to delete the address
            if (!authorizeUser(userId, token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .build();
            }

           addressRepository.deleteById(addressId);
           log.info("Address with id {} deleted", addressId);
           return ResponseEntity.accepted()
                   .build();
    }

    private boolean authorizeUser(UUID userId, String token) throws Exception {
        if (JWTUtils.getUserIdFromToken(token).equals(userId)) {
            return true;
        }
        return false;
    }


}
