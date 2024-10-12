package com.neighborhoodservice.user.controller;

import com.neighborhoodservice.user.authorizationUtils.JWTUtils;
import com.neighborhoodservice.user.dto.RegisterDto;
import com.neighborhoodservice.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JWTUtils JWTUtils;

    @PostMapping
    public ResponseEntity<UUID> createUser(@RequestBody @Valid RegisterDto registerDto,
                                         @RequestHeader("Authorization") String token
    ) throws Exception {
        if (!JWTUtils.hasAdminRole(token)){
            return ResponseEntity.status(403).build();
        }
       return ResponseEntity.ok(userService.registerUser(registerDto));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(@PathVariable UUID userId) {
        return ok(userService.getUserById(userId));
    }


}
