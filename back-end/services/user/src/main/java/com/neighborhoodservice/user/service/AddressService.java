package com.neighborhoodservice.user.service;

import com.neighborhoodservice.user.dto.AddressPatchRequest;
import com.neighborhoodservice.user.dto.AddressResponse;
import com.neighborhoodservice.user.exception.ResourceNotFoundException;
import com.neighborhoodservice.user.model.Address;
import com.neighborhoodservice.user.model.User;
import com.neighborhoodservice.user.repository.AddressRepository;
import com.neighborhoodservice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AddressService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    public void addAddress(UUID userId, AddressPatchRequest addressPatchRequest) {

//        Check if user exists
        User user = userRepository.findById(userId)
                .orElseThrow( () -> new ResourceNotFoundException("User with id " + userId + " not found"));

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

        addressRepository.save(address);
    }


    public List<AddressResponse> getAllAddresses(UUID userId) {

            User user = userRepository.findById(userId)
                    .orElseThrow( () -> new ResourceNotFoundException("User with id " + userId + " not found"));

            List<Address> addresses = addressRepository.findAllByUser(user);

            return addresses.stream()
                    .map(address -> new AddressResponse(
                            address.getAddress(),
                            address.getCity(),
                            address.getPostalCode(),
                            address.getAddressType().toString(),
                            address.isDefault()
                    ))
                    .toList();
    }
}
