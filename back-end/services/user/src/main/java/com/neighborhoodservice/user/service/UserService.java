package com.neighborhoodservice.user.service;

import com.neighborhoodservice.user.dto.RegisterDto;
import com.neighborhoodservice.user.model.User;
import com.neighborhoodservice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    public UUID registerUser(RegisterDto registerDto) {

        User user = User.builder()
                .userId(registerDto.id())
                .email(registerDto.email())
                .build();

        userRepository.save(user);
        return user.getUserId();
    }

}
