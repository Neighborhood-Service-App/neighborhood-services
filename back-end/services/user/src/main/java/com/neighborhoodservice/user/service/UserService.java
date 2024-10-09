package com.neighborhoodservice.user.service;

import com.neighborhoodservice.user.LoginRequest;
import com.neighborhoodservice.user.model.User;
import com.neighborhoodservice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public ResponseEntity<Long> authenticate(LoginRequest loginRequest) {

        Optional<User> optionalUser = userRepository.findByEmail(loginRequest.email());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (loginRequest.password().equals(user.getPassword())) {
                return ResponseEntity.ok(user.getUserId());
            } else {
                return ResponseEntity.badRequest().build();
            }
        }

        return ResponseEntity.badRequest().build();
    }

}
