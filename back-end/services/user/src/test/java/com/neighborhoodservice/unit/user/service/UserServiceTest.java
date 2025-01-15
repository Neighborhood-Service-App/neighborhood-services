package com.neighborhoodservice.unit.user.service;

import com.neighborhoodservice.user.dto.*;
import com.neighborhoodservice.user.exception.ResourceAlreadyExistsException;
import com.neighborhoodservice.user.exception.ResourceNotFoundException;
import com.neighborhoodservice.user.model.User;
import com.neighborhoodservice.user.repository.UserRepository;
import com.neighborhoodservice.user.service.AwsService;
import com.neighborhoodservice.user.service.CloudFrontService;
import com.neighborhoodservice.user.service.KeycloakService;
import com.neighborhoodservice.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
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

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UserServiceTest {

    @Mock(lenient = true)
    private UserRepository userRepository;

    @Mock(lenient = true)
    private AwsService awsService;

    @Mock(lenient = true)
    private CloudFrontService cloudFrontService;

    @Mock(lenient = true)
    UserMapper userMapper;

    @Mock(lenient = true)
    UserPatchMapper userPatchMapper;

    @Mock(lenient = true)
    private KeycloakService keyCloakService;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private RegisterRequest registerRequest;
    private UserPatchRequest userPatchRequest;



    @BeforeEach
    void setUp() {

        // Setup a test user
        testUser = new User();
        testUser.setUserId(UUID.randomUUID());
        testUser.setEmail("test@example.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");

        // Setup register request
        registerRequest = new RegisterRequest("John", "Doe", "password", "test@example.com");

        // Setup user patch request
        userPatchRequest = new UserPatchRequest("newFirstName", "newLastName", "+380675757500", "Simple about");
    }

    @Test
    void registerUser_Success() {
        // Arrange: Mock the repository and keycloak service responses
        when(userRepository.existsByEmail(registerRequest.email())).thenReturn(false);

        doAnswer(invocation -> "admin-jwt").when(keyCloakService).getAdminJwtToken();

        when(keyCloakService.getUserIdByEmail(anyString(), anyString())).thenReturn(UUID.randomUUID().toString());
        doNothing().when(keyCloakService).createUser(anyString(), any(RegisterKeycloakRequest.class));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act: Call the registerUser method
        UUID userId = userService.registerUser(registerRequest);

        // Assert: Verify that everything was called the correct number of times
        assertNotNull(userId);
        verify(userRepository, times(1)).save(any(User.class));
        verify(keyCloakService, times(1)).getAdminJwtToken();
        verify(keyCloakService, times(1)).createUser(anyString(), any(RegisterKeycloakRequest.class));
        verify(keyCloakService, times(1)).getUserIdByEmail(anyString(), anyString());
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
        userService.deleteUser(testUser.getUserId());

        // Assert
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
                new ArrayList<>()
        );

        // Mock the findById method to return the original test user
        when(userRepository.findById(testUser.getUserId())).thenReturn(Optional.of(testUser));

        // Mock the updateUserFromDto method to update the test user fields
        doAnswer(invocation -> {
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
                        ""
                )
        );

        // Act
        UserResponse result = userService.updateUser(testUser.getUserId(), userPatchRequest);

        // Assert
        assertNotNull(result);
        assertEquals(updatedUser.getUserId(), result.userId());
        assertEquals(userPatchRequest.firstName(), result.firstName());
        assertEquals(userPatchRequest.lastName(), result.lastName());
        assertEquals(userPatchRequest.phoneNumber(), result.phoneNumber());
        assertEquals(userPatchRequest.about(), result.about());

        verify(userRepository, times(1)).save(eq(updatedUser));
        verify(userPatchMapper, times(1)).updateUserFromDto(userPatchRequest, testUser);
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
        ResponseEntity<HttpStatus> result = userService.deleteProfilePicture(testUser.getUserId());

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