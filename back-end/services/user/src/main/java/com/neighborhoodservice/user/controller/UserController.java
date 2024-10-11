package com.neighborhoodservice.user.controller;

import com.neighborhoodservice.user.dto.RegisterDto;
import com.neighborhoodservice.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UUID> createUser(@RequestBody RegisterDto registerDto) {
       return ResponseEntity.ok(userService.registerUser(registerDto));
    }

    @GetMapping
    public String getUser() {
        return "Hello";
    }


}
