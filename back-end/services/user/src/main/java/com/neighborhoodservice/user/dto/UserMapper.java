package com.neighborhoodservice.user.dto;

import com.neighborhoodservice.user.model.User;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserMapper {





    public UserResponse fromUser(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getAbout(),
                user.getCreatedAt(),
                user.getLastUpdatedAt(),
                null
        );
    }

}
