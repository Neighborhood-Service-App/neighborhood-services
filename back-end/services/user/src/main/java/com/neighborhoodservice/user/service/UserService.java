package com.neighborhoodservice.user.service;

import com.neighborhoodservice.user.dto.RegisterDto;
import com.neighborhoodservice.user.exception.UserAlreadyExistsException;
import com.neighborhoodservice.user.model.User;
import com.neighborhoodservice.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UUID registerUser(RegisterDto registerDto) {

        User user = User.builder()
                .userId(registerDto.id())
                .email(registerDto.email())
                .addresses(new ArrayList<>())
                .build();

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + user.getEmail() + " already exists");
        }
        userRepository.save(user);
        log.info("User with id {} has been registered", user.getUserId());
        return user.getUserId();
    }

    public User getUserById(UUID userId) {
        return userRepository.findById(userId).orElse(null);
    }

}
