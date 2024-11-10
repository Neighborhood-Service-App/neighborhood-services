package com.neighborhoodservice.user.service;

import com.neighborhoodservice.user.dto.*;
import com.neighborhoodservice.user.exception.ResourceAlreadyExistsException;
import com.neighborhoodservice.user.exception.ResourceNotFoundException;
import com.neighborhoodservice.user.model.User;
import com.neighborhoodservice.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserPatchMapper userPatchMapper;
    private final AwsService awsService;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;


    @Transactional
    public UUID registerUser(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email()) || userRepository.existsById(UUID.fromString(request.id()))) {
            throw new ResourceAlreadyExistsException("User with email " + request.email() + " or id " + request.id() +" already exists");
        }

        User user = userMapper.toUser(request);

        userRepository.save(user);
        log.info("User with id {} has been registered", user.getUserId());
        return user.getUserId();
    }

    public UserResponse getUserById(UUID userId) {
//        TODO: Add information about the user's ratings and jobs(OpenFeign)
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


    @Transactional
    public UserResponse updateUser(UUID userId, UserPatchRequest userPatchRequest) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));

//        TODO: Validate address in userPatchRequest
        userPatchMapper.updateUserFromDto(userPatchRequest, user);

        return userMapper.fromUser(userRepository.save(user));
    }


    public ResponseEntity<String> updateProfilePicture(UUID userId, MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String contentType = file.getContentType();
        long fileSize = file.getSize();
        InputStream inputStream = file.getInputStream();

        awsService.uploadFile(bucketName, fileName, fileSize, contentType, inputStream);

        return ResponseEntity.ok().body("File uploaded successfully");
    }

    public ResponseEntity<String> deleteProfilePicture(UUID userId) {

        awsService.deleteFile(bucketName, userId.toString());
        return ResponseEntity.ok().body("File deleted successfully");
    }
}
