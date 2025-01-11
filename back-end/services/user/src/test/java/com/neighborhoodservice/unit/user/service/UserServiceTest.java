package com.neighborhoodservice.unit.user.service;

import com.neighborhoodservice.user.dto.*;
import com.neighborhoodservice.user.exception.ResourceAlreadyExistsException;
import com.neighborhoodservice.user.exception.ResourceNotFoundException;
import com.neighborhoodservice.user.model.User;
import com.neighborhoodservice.user.repository.UserRepository;
import com.neighborhoodservice.user.service.AwsService;
import com.neighborhoodservice.user.service.CloudFrontService;
import com.neighborhoodservice.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;


@ActiveProfiles("test")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AwsService awsService;

    @Mock
    private CloudFrontService cloudFrontService;

    @Mock
    UserMapper userMapper;

    @Mock
    UserPatchMapper userPatchMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private RegisterRequest registerRequest;
    private UserPatchRequest userPatchRequest;
    private UserResponse userResponse;





    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup a test user
        testUser = new User();
        testUser.setUserId(UUID.randomUUID());
        testUser.setEmail("test@example.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");

        // Setup register request
        registerRequest = new RegisterRequest(UUID.randomUUID().toString(), "test@example.com");

        // Setup user patch request
        userPatchRequest = new UserPatchRequest("newFirstName", "newLastName", "+380675757500", "Simple about");
    }

    @Test
    void registerUser_Success() {
        // Arrange
        when(userRepository.existsByEmail(registerRequest.email())).thenReturn(false);
        when(userRepository.existsById(UUID.fromString(registerRequest.id()))).thenReturn(false);
        when(userMapper.toUser(registerRequest)).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        UUID result = userService.registerUser(registerRequest);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getUserId(), result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_EmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByEmail(registerRequest.email())).thenReturn(true);

        // Act & Assert
        assertThrows(ResourceAlreadyExistsException.class, () -> userService.registerUser(registerRequest));
    }

    @Test
    void getUserById_Success() {
        // Arrange
        when(userRepository.findById(testUser.getUserId())).thenReturn(Optional.of(testUser));
        when(awsService.doesObjectExist(anyString(), anyString())).thenReturn(true);
        when(cloudFrontService.generateSignedUrl(anyString(), anyInt())).thenReturn("signedUrl");

        UserResponse userResponse = new UserResponse(
                testUser.getUserId(),
                testUser.getFirstName(),
                testUser.getLastName(),
                testUser.getEmail(),
                testUser.getPhoneNumber(),
                testUser.getAbout(),
                testUser.getCreatedAt(),
                testUser.getLastUpdatedAt(),
                ""
        );
        when(userMapper.fromUser(testUser)).thenReturn(userResponse);



        // Act
        UserResponse result = userService.getUserById(testUser.getUserId());

        // Assert
        assertNotNull(result);
        assertEquals(userResponse, result);
        verify(userRepository, times(1)).findById(testUser.getUserId());
    }

    @Test
    void getUserById_UserNotFound() {
        // Arrange
        when(userRepository.findById(testUser.getUserId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(testUser.getUserId()));
    }

    @Test
    void deleteUser_Success() {
        // Arrange
        when(userRepository.findById(testUser.getUserId())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        UUID result = userService.deleteUser(testUser.getUserId());

        // Assert
        assertEquals(testUser.getUserId(), result);
        verify(userRepository, times(1)).delete(testUser);
        verify(awsService, times(1)).deleteFile(any(), any());
    }

    @Test
    void deleteUser_UserNotFound() {
        // Arrange
        when(userRepository.findById(testUser.getUserId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(testUser.getUserId()));
    }

    @Test
    void updateUser_Success() {
        // Arrange

        // Create an updated version of the user with the fields from the patch request
        User updatedUser = new User(
                testUser.getUserId(),
                userPatchRequest.firstName(),
                userPatchRequest.lastName(),
                testUser.getEmail(),
                userPatchRequest.phoneNumber(),
                userPatchRequest.about(),
                testUser.getCreatedAt(),
                testUser.getLastUpdatedAt(),
                // Mock the addresses field as empty or null (based on your actual model)
                new ArrayList<>() // Empty list to avoid the addresses mismatch
        );

        // Mock the findById method to return the original test user
        when(userRepository.findById(testUser.getUserId())).thenReturn(Optional.of(testUser));

        // Mock the updateUserFromDto method to update the test user fields
        doAnswer(invocation -> {
            // Simulate the update of user fields using the patch request
            UserPatchRequest patchRequest = invocation.getArgument(0);
            testUser.setFirstName(patchRequest.firstName());
            testUser.setLastName(patchRequest.lastName());
            testUser.setPhoneNumber(patchRequest.phoneNumber());
            testUser.setAbout(patchRequest.about());
            return null;
        }).when(userPatchMapper).updateUserFromDto(userPatchRequest, testUser);

        // Mock the save method to return the updated user after save
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // Mock the userMapper to return a UserResponse based on the updated user
        when(userMapper.fromUser(updatedUser)).thenReturn(
                new UserResponse(
                        updatedUser.getUserId(),
                        updatedUser.getFirstName(),
                        updatedUser.getLastName(),
                        updatedUser.getEmail(),
                        updatedUser.getPhoneNumber(),
                        updatedUser.getAbout(),
                        updatedUser.getCreatedAt(),
                        updatedUser.getLastUpdatedAt(),
                        "" // Assuming an empty profile URL for simplicity
                )
        );

        // Act
        UserResponse result = userService.updateUser(testUser.getUserId(), userPatchRequest);

        // Assert
        // Ensure the result is not null
        assertNotNull(result);

        // Assert that the fields in the response match the updated values
        assertEquals(updatedUser.getUserId(), result.userId());  // Ensure the userId is correct
        assertEquals(userPatchRequest.firstName(), result.firstName());  // Ensure the first name is updated
        assertEquals(userPatchRequest.lastName(), result.lastName());  // Ensure the last name is updated
        assertEquals(userPatchRequest.phoneNumber(), result.phoneNumber());  // Ensure the phone number is updated
        assertEquals(userPatchRequest.about(), result.about());  // Ensure the about field is updated

        // Verify save was called with the updated user object
        verify(userRepository, times(1)).save(eq(updatedUser));  // Verify save was called with the updated user

        // Verify the updateUserFromDto method was called with the correct arguments
        verify(userPatchMapper, times(1)).updateUserFromDto(userPatchRequest, testUser);  // Ensure patching method was invoked
    }





    @Test
    void updateUser_UserNotFound() {
        // Arrange
        when(userRepository.findById(testUser.getUserId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(testUser.getUserId(), userPatchRequest));
    }

    @Test
    void updateProfilePicture_Success() throws IOException {
        // Arrange
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getSize()).thenReturn(1024L);
        when(mockFile.getContentType()).thenReturn("image/jpeg");
        when(mockFile.getInputStream()).thenReturn(mock(InputStream.class));

        when(userRepository.findById(testUser.getUserId())).thenReturn(Optional.of(testUser));
        doNothing().when(awsService).uploadFile(any(), any(), any(), any(), any());

        // Act
        ResponseEntity<String> result = userService.updateProfilePicture(testUser.getUserId(), mockFile);

        // Assert
        assertEquals(200, result.getStatusCodeValue());
        verify(awsService, times(1)).uploadFile(any(), any(), any(), any(), any());
    }

    @Test
    void updateProfilePicture_FileEmpty() throws IOException {
        // Arrange
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(true);

        // Act
        ResponseEntity<String> result = userService.updateProfilePicture(testUser.getUserId(), mockFile);

        // Assert
        assertEquals(400, result.getStatusCodeValue());
    }

    @Test
    void deleteProfilePicture_Success() {
        // Arrange
        when(userRepository.findById(testUser.getUserId())).thenReturn(Optional.of(testUser));
        doNothing().when(awsService).deleteFile(any(), any());

        // Act
        ResponseEntity<String> result = userService.deleteProfilePicture(testUser.getUserId());

        // Assert
        assertEquals(200, result.getStatusCodeValue());
        verify(awsService, times(1)).deleteFile(any(), any());
    }

    @Test
    void deleteProfilePicture_UserNotFound() {
        // Arrange
        when(userRepository.findById(testUser.getUserId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteProfilePicture(testUser.getUserId()));
    }
}
