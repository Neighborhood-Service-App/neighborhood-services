package com.neighborhoodservice.user.controller;

import com.neighborhoodservice.user.LoginRequest;
import com.neighborhoodservice.user.model.User;
import com.neighborhoodservice.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/authenticate")
    public ResponseEntity<Long> authenticate(@RequestBody LoginRequest loginRequest) {

        Long userId = userService.authenticate(loginRequest).getBody();

        return ResponseEntity.ok(userId);

    }

}
