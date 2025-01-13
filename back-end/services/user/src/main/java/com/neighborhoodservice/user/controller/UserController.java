package com.neighborhoodservice.user.controller;

import com.neighborhoodservice.user.authorizationUtils.JWTUtils;
import com.neighborhoodservice.user.dto.LoginRequest;
import com.neighborhoodservice.user.dto.RegisterRequest;
import com.neighborhoodservice.user.dto.UserPatchRequest;
import com.neighborhoodservice.user.dto.UserResponse;
import com.neighborhoodservice.user.service.UserService;
import com.neighborhoodservice.user.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final JWTUtils JWTUtils;

    @PostMapping("/register")
    public ResponseEntity<UUID> createUser(
            @RequestBody @Valid RegisterRequest registerRequest
    ) throws Exception {


        return ok(userService.registerUser(registerRequest));

    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID userId) {
        return ok(userService.getUserById(userId));
    }

    @DeleteMapping("/me")
    public ResponseEntity<HttpStatus> deleteUser(
            @RequestHeader("Authorization") String token
    )throws Exception {

        UUID userId = JWTUtils.getUserIdFromToken(token);

        log.info("Deleting user with id {}", userId);

        userService.deleteUser(userId);

        return noContent().build();

    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateUser(
            @RequestBody @Valid UserPatchRequest userPatchRequest,
            @RequestHeader("Authorization") String token
    ) throws Exception {

        UUID userId = JWTUtils.getUserIdFromToken(token);

        log.info("Updating user with id {}", userId);

        return ok(userService.updateUser(userId, userPatchRequest));

    }


    @PostMapping("/profile-picture")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String token
    ) throws Exception {

        UUID userId = JWTUtils.getUserIdFromToken(token);
        return userService.updateProfilePicture(userId, file);

    }

    // Endpoint to delete a file from a bucket
    @DeleteMapping("/profile-picture")
    public ResponseEntity<?> deleteFile(
            @RequestHeader("Authorization") String token
    ) throws Exception {

        UUID userId = JWTUtils.getUserIdFromToken(token);
        return userService.deleteProfilePicture(userId);

    }


    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody @Valid LoginRequest loginRequest
    ) throws Exception {

        return ok(userService.login(loginRequest));

    }


}
