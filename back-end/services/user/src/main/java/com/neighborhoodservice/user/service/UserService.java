package com.neighborhoodservice.user.service;

import com.neighborhoodservice.user.dto.RegisterRequest;
import com.neighborhoodservice.user.dto.UserMapper;
import com.neighborhoodservice.user.dto.UserResponse;
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
    private final UserMapper userMapper;


    @Transactional
    public UUID registerUser(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email()) || userRepository.existsById(request.id())) {
            throw new ResourceAlreadyExistsException("User with email " + request.email() + " already exists");
        }

        User user = userMapper.toUser(request);

        userRepository.save(user);
        log.info("User with id {} has been registered", user.getUserId());
        return user.getUserId();
    }

    public UserResponse getUserById(UUID userId) {
        return userRepository.findById(userId)
                .map(userMapper::fromUser)
                .orElseThrow( () -> new ResourceNotFoundException("User with id " + userId + " not found"));
    }

    @Transactional
    public UUID deleteUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow( () -> new ResourceNotFoundException("User with id " + userId + " not found"));

        userRepository.delete(user);
        log.info("User with id {} has been deleted", userId);
        return userId;
    }
}
