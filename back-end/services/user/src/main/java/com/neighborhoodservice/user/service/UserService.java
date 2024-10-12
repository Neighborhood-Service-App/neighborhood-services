package com.neighborhoodservice.user.service;

import com.neighborhoodservice.user.dto.RegisterDto;
import com.neighborhoodservice.user.exception.ResourceAlreadyExistsException;
import com.neighborhoodservice.user.exception.ResourceNotFoundException;
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

        if (userRepository.existsByEmail(user.getEmail()) || userRepository.existsById(user.getUserId())) {
            throw new ResourceAlreadyExistsException("User with email " + user.getEmail() + " already exists");
        }
        userRepository.save(user);
        log.info("User with id {} has been registered", user.getUserId());
        return user.getUserId();
    }

    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow( () -> new ResourceNotFoundException("User with id " + userId + " not found"));
    }

}
