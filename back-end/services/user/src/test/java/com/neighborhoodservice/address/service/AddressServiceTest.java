package com.neighborhoodservice.address.service;

import com.neighborhoodservice.user.dto.AddressPatchMapper;
import com.neighborhoodservice.user.dto.AddressRequest;
import com.neighborhoodservice.user.dto.AddressResponse;
import com.neighborhoodservice.user.exception.ResourceNotFoundException;
import com.neighborhoodservice.user.model.Address;
import com.neighborhoodservice.user.model.AddressType;
import com.neighborhoodservice.user.model.User;
import com.neighborhoodservice.user.repository.AddressRepository;
import com.neighborhoodservice.user.repository.UserRepository;
import com.neighborhoodservice.user.service.GeocodingService;
import com.neighborhoodservice.user.service.impl.AddressServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ActiveProfiles("test")
public class AddressServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private GeocodingService geocodingService;

    @Mock
    private AddressPatchMapper addressPatchMapper;

    @InjectMocks
    private AddressServiceImpl addressService;

    private User testUser;
    private Address testAddress;
    private AddressRequest addressRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setUserId(UUID.randomUUID());


        testAddress = new Address();
        testAddress.setAddressId(1L);
        testAddress.setCity("Kyiv");
        testAddress.setPostalCode("04201");
        testAddress.setAddress("123 Main Street");
        testAddress.setAddressType(AddressType.HOME);
        testAddress.setUser(testUser);

        addressRequest = new AddressRequest("Kyiv", "04201", "123 Main Street", AddressType.HOME, true);
    }

    @Test
    void addAddress_Success() throws Exception {
        when(userRepository.findById(testUser.getUserId())).thenReturn(Optional.of(testUser));
        when(addressRepository.findAllByUser(testUser)).thenReturn(Collections.emptyList());
        when(geocodingService.getCoordinates(anyString())).thenReturn(Map.of("lat", 50.4501, "lng", 30.5234));
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);

        ResponseEntity<HttpStatus> response = addressService.addAddress(testUser.getUserId(), addressRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getHeaders().getLocation().toString().contains("/api/v1/users/" + testUser.getUserId() + "/addresses/"));
    }

    @Test
    void addAddress_UserNotFound() {
        when(userRepository.findById(testUser.getUserId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            addressService.addAddress(testUser.getUserId(), addressRequest);
        });
    }

    @Test
    void getAllAddresses_Success() {
        when(userRepository.findById(testUser.getUserId())).thenReturn(Optional.of(testUser));
        when(addressRepository.findAllByUser(testUser)).thenReturn(List.of(testAddress));

        List<AddressResponse> addresses = addressService.getAllAddresses(testUser.getUserId());

        assertEquals(1, addresses.size());
    }

    @Test
    void deleteAddressById_Success() {
        when(userRepository.existsById(testUser.getUserId())).thenReturn(true);
        when(addressRepository.findById(testAddress.getAddressId())).thenReturn(Optional.of(testAddress));

        ResponseEntity<HttpStatus> response = addressService.deleteAddressById(testUser.getUserId(), testAddress.getAddressId());

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(addressRepository, times(1)).deleteById(testAddress.getAddressId());
    }

    @Test
    void updateAddress_Success() {
        when(userRepository.existsById(testUser.getUserId())).thenReturn(true);
        when(addressRepository.findById(testAddress.getAddressId())).thenReturn(Optional.of(testAddress));

        ResponseEntity<HttpStatus> response = addressService.updateAddress(testUser.getUserId(), testAddress.getAddressId(), addressRequest);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        verify(addressPatchMapper, times(1)).updateAddressFromDto(addressRequest, testAddress);
        verify(addressRepository, times(1)).save(testAddress);
    }
}
