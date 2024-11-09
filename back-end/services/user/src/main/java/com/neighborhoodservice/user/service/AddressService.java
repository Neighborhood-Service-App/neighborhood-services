package com.neighborhoodservice.user.service;

import com.neighborhoodservice.user.authorizationUtils.JWTUtils;
import com.neighborhoodservice.user.dto.AddressPatchMapper;
import com.neighborhoodservice.user.dto.AddressRequest;
import com.neighborhoodservice.user.dto.AddressResponse;
import com.neighborhoodservice.user.exception.AuthorizationException;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AddressService {

    private static final Logger log = LoggerFactory.getLogger(AddressService.class);
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final JWTUtils JWTUtils;
    private final GeocodingService geocodingService;
    private final AddressPatchMapper addressPatchMapper;

    public ResponseEntity<HttpStatus> addAddress(UUID userId, AddressRequest addressRequest, String token) throws Exception {

//        Check if user exists
        User user = userRepository.findById(userId)
                .orElseThrow( () -> new ResourceNotFoundException("User with id " + userId + " not found"));

//        Check if user is authorized to add an address
        authorizeUser(userId, token);


//        Add logic for checking if user has an address with the same address type or already 3 addresses
        List<Address> addresses = addressRepository.findAllByUser(user);
        if (addresses.size() >= 3) {
            throw new ResourceAlreadyExistsException("User with id " + userId + " already has 3 addresses");
        }
        for (Address address : addresses) {
            if (address.getAddressType().equals(addressRequest.addressType())) {
                throw new ResourceAlreadyExistsException("User with id " + userId + " already has an address with type " + addressRequest.addressType());
            }
        }

        Map<String, Double> coordinates = geocodingService
                .getCoordinates(addressRequest.address() + ", " + addressRequest.city() + ", " + "Ukraine" + ", " + addressRequest.postalCode());
        Double lat = coordinates.get("lat");
        Double lng = coordinates.get("lng");

        // Add address to the user
        Address address = new Address(
                user,
                addressRequest.address(),
                addressRequest.city(),
                addressRequest.postalCode(),
                lat,
                lng,
                addressRequest.addressType(),
                addressRequest.isDefault()
        );

//        Save address to the database
        addressRepository.save(address);
        return ResponseEntity.accepted()
                .build();
    }


    public List<AddressResponse> getAllAddresses(UUID userId,String token) throws Exception {


            User user = userRepository.findById(userId)
                    .orElseThrow( () -> new ResourceNotFoundException("User with id " + userId + " not found"));

            authorizeUser(userId, token);

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
            checkIfUserExists(userId);

//            Check if address exists and if so, store in variable
            Address address = getAddressById(addressId);

//            Check if address belongs to the user
            checkIfAddressBelongsToUser(userId, addressId, address);

//            Check if user is authorized to delete the address
            authorizeUser(userId, token);

           addressRepository.deleteById(addressId);
           log.info("Address with id {} deleted", addressId);
           return ResponseEntity.accepted()
                   .build();
    }


    public ResponseEntity<HttpStatus> updateAddress(
            UUID userId, 
            Long addressId, 
            AddressRequest addressRequest, 
            String token) throws Exception {

//            Check if user exists
        checkIfUserExists(userId);
        
//            Check if address exists and if so, store in variable
        Address address = getAddressById(addressId);
        
//            Check if address belongs to the user
        checkIfAddressBelongsToUser(userId, addressId, address);

//            Check if user is authorized to update the address
        authorizeUser(userId, token);

        addressPatchMapper.updateAddressFromDto(addressRequest, address);
        addressRepository.save(address);
        log.info("Address with id {} updated", addressId);
        return ResponseEntity.accepted()
                .build();

    }

    private void checkIfAddressBelongsToUser(UUID userId, Long addressId, Address address) {
        if (!address.getUser().getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Address with id " + addressId + " is not found for user with id " + userId);
        }
    }


    private void checkIfUserExists(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User with id " + userId + " not found");
        }
    }
    
    
    private boolean authorizeUser(UUID userId, String token) throws Exception {
        if (JWTUtils.getUserIdFromToken(token).equals(userId)) {
            return true;
        }
        throw new AuthorizationException("User with id " + userId + " is not authorized to perform this action");
    }
    
    private Address getAddressById(Long addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow( () -> new ResourceNotFoundException("Address with id " + addressId + " not found"));
    }
    
    
}
