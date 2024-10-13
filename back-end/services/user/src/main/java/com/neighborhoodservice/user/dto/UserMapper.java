package com.neighborhoodservice.user.dto;

import com.neighborhoodservice.user.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {

    public User toUser(RegisterRequest registerRequest) {
        return User.builder()
                .userId(registerRequest.id())
                .email(registerRequest.email())
                .build();

    }

    public UserResponse fromUser(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getAbout(),
                user.getAddresses(),
                user.getCreatedAt(),
                user.getLastUpdatedAt(),
                user.getImgUrl()
        );
    }

}
