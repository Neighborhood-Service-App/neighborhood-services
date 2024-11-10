package com.neighborhoodservice.user.controller;

import com.neighborhoodservice.user.authorizationUtils.JWTUtils;
import com.neighborhoodservice.user.dto.RegisterRequest;
import com.neighborhoodservice.user.dto.UserPatchRequest;
import com.neighborhoodservice.user.dto.UserResponse;
import com.neighborhoodservice.user.enumeration.FileType;
import com.neighborhoodservice.user.service.AwsService;
import com.neighborhoodservice.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final JWTUtils JWTUtils;

    @PostMapping
    public ResponseEntity<UUID> createUser(
            @RequestBody @Valid RegisterRequest registerRequest,
            @RequestHeader("Authorization") String token
    ) throws Exception {

        JWTUtils.hasAdminRole(token);

        return ok(userService.registerUser(registerRequest));

    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID userId) {
        return ok(userService.getUserById(userId));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<UUID> deleteUser(
            @PathVariable UUID userId,
            @RequestHeader("Authorization") String token
    )throws Exception {

//        Only admin can delete users
        JWTUtils.hasAdminRole(token);

        log.info("Deleting user with id {}", userId);

        return ok(userService.deleteUser(userId));

    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID userId,
            @RequestBody @Valid UserPatchRequest userPatchRequest,
            @RequestHeader("Authorization") String token
    ) throws Exception {

        JWTUtils.authorizeUser(userId, token);

        log.info("Updating user with id {}", userId);

        return ok(userService.updateUser(userId, userPatchRequest));

    }


    @PostMapping("/{userId}/profile-picture")
    public ResponseEntity<?> uploadFile(
            @PathVariable("userId") UUID userId,
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String token
    ) throws Exception {

        JWTUtils.authorizeUser(userId, token);
        return userService.updateProfilePicture(userId, file);

    }
//    TODO: Add endpoint to get a file from a bucket(or from cache if it exists)

    // Endpoint to delete a file from a bucket
    @DeleteMapping("/{userId}/profile-picture")
    public ResponseEntity<?> deleteFile(
            @PathVariable("userId") UUID userId,
            @RequestHeader("Authorization") String token
    ) throws Exception {

        JWTUtils.authorizeUser(userId, token);
        return userService.deleteProfilePicture(userId);

    }


}
