package com.neighborhoodservice.user.service.impl;

import com.neighborhoodservice.user.dto.*;
import com.neighborhoodservice.user.exception.ResourceAlreadyExistsException;
import com.neighborhoodservice.user.exception.ResourceNotFoundException;
import com.neighborhoodservice.user.model.User;
import com.neighborhoodservice.user.repository.UserRepository;
import com.neighborhoodservice.user.service.AwsService;
import com.neighborhoodservice.user.service.CloudFrontService;
import com.neighborhoodservice.user.service.KeycloakService;
import com.neighborhoodservice.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Primary
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserPatchMapper userPatchMapper;
    private final AwsService awsService;
    private final CloudFrontService cloudFrontService;
    private final KeycloakService keyCloakService;


    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;




//  TODO : Enable account in keycloak
    @Override
    @Transactional
    public UUID registerUser(RegisterRequest request) {

        if ( userRepository.existsByEmail(request.email()) ) {
            throw new ResourceAlreadyExistsException("User with email " + request.email()+ " already exists");
        }


        String adminJWT = keyCloakService.getAdminJwtToken();
        log.debug("Got the admin JWT");

        RegisterKeycloakRequest registerKeycloakRequest = new RegisterKeycloakRequest(
                true,
                request.firstName(),
                request.lastName(),
                request.email(),
                List.of(new RegisterKeycloakRequest.Credentials(request.password()))
        );

        keyCloakService.createUser(adminJWT, registerKeycloakRequest);
        log.info("Registered user with email {} in keycloak successfully", request.email());

//      Get the user's generated UUID from keycloak
        String userId = keyCloakService.getUserIdByEmail(request.email(), adminJWT);

        User user = User.builder()
                .userId(UUID.fromString(userId))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .build();

        log.info("Retrieved UUID for user with email {}", request.email());

//        Save the user to the database
        userRepository.save(user);
        log.info("User with id {} has been registered", user.getUserId());

        keyCloakService.sendVerificationEmail(adminJWT, userId);

        return user.getUserId();
    }

    @Override
    public Object login(LoginRequest loginRequest) {
        return keyCloakService.login(keyCloakService.getAdminJwtToken(), loginRequest);
    }


    @Override
    public UserResponse getUserById(UUID userId) {
//        TODO: Add information about the user's ratings and jobs(OpenFeign)
        log.info("Fetching user with id {}", userId);

//        Get the user from the database
        UserResponse userResponse = userRepository.findById(userId)
                .map(userMapper::fromUser)
                .orElseThrow( () -> new ResourceNotFoundException("User with id " + userId + " not found"));

//        TODO : Add caching for the user's profile picture
//      Generate a signed URL for the user's profile picture
        String signedUrl = "";
        if (awsService.doesObjectExist(bucketName, userId.toString())) {
            signedUrl = generateSignedUrl(userId.toString());
        }

//        Return the user response with the signed URL of the user's profile picture
        return new UserResponse(userResponse, signedUrl);
    }

    @Override
    @Transactional
    public UUID deleteUser(UUID userId) {
//      TODO: Delete all the jobs and ratings associated with the user

        User user = userRepository.findById(userId)
                .orElseThrow( () -> new ResourceNotFoundException("User with id " + userId + " not found"));

//      Delete the user
        userRepository.delete(user);

//      Delete the user's profile picture from S3
        awsService.deleteFile(bucketName, userId.toString());
        log.info("User with id {} has been deleted", userId);
        return userId;
    }


    @Override
    @Transactional
    public UserResponse updateUser(UUID userId, UserPatchRequest userPatchRequest) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));

        userPatchMapper.updateUserFromDto(userPatchRequest, user);

        return userMapper.fromUser(userRepository.save(user));
    }


    @Override
    public ResponseEntity<String> updateProfilePicture(UUID userId, MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }


        String contentType = file.getContentType();
        if (!(contentType.startsWith("image/") || contentType.equals("application/pdf"))) {
            return ResponseEntity.badRequest().body("Invalid file type. Only images and PDF are allowed.");
        }

        long fileSize = file.getSize();
        InputStream inputStream = file.getInputStream();

        awsService.uploadFile(bucketName, userId.toString(), fileSize, contentType, inputStream);

        User user = userRepository.findById(userId)
                .orElseThrow(
                () -> new ResourceNotFoundException("User with ID " + userId + " not found")
        );


        userRepository.save(user);

        return ResponseEntity.ok().body("File uploaded successfully");

    }

    @Override
    public ResponseEntity<String> deleteProfilePicture(UUID userId) {

        awsService.deleteFile(bucketName, userId.toString());

        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("User with ID " + userId + " not found")
       );


        userRepository.save(user);

        return ResponseEntity.ok().body("File deleted successfully");
    }



    private String generateSignedUrl(String keyName) {
        return cloudFrontService.generateSignedUrl(keyName, 60);
    }

}
