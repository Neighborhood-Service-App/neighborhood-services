package com.neighborhoodservice.user.controller;

import com.neighborhoodservice.user.authorizationUtils.JWTUtils;
import com.neighborhoodservice.user.dto.RegisterRequest;
import com.neighborhoodservice.user.dto.UserPatchRequest;
import com.neighborhoodservice.user.dto.UserResponse;
import com.neighborhoodservice.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        if (!JWTUtils.hasAdminRole(token)){
            return ResponseEntity.status(403).build();
        }
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

        if (!JWTUtils.hasAdminRole(token)){
            return ResponseEntity.status(403).build();
        }
        log.info("Deleting user with id {}", userId);
        return ok(userService.deleteUser(userId));

    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID userId,
            @RequestBody @Valid UserPatchRequest userPatchRequest,
            @RequestHeader("Authorization") String token
    ) throws Exception {

        if (!JWTUtils.getUserIdFromToken(token).equals(userId)){
            return ResponseEntity.status(403).build();
        }
        log.info("Updating user with id {}", userId);
        return ok(userService.updateUser(userId, userPatchRequest));

    }


}
