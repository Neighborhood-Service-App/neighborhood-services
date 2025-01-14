package com.neighborhoodservice.user.service;

import com.neighborhoodservice.user.dto.LoginRequest;
import com.neighborhoodservice.user.dto.RegisterRequest;
import com.neighborhoodservice.user.dto.UserPatchRequest;
import com.neighborhoodservice.user.dto.UserResponse;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface UserService {
    @Transactional
    UUID registerUser(RegisterRequest request);

    UserResponse getUserById(UUID userId);

    @Transactional
    void deleteUser(UUID userId);

    @Transactional
    UserResponse updateUser(UUID userId, UserPatchRequest userPatchRequest);

    ResponseEntity<String> updateProfilePicture(UUID userId, MultipartFile file) throws IOException;

    ResponseEntity<HttpStatus> deleteProfilePicture(UUID userId);

    Object login(LoginRequest loginRequest);
}
