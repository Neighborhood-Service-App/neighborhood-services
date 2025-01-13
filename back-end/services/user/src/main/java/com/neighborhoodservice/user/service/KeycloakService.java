package com.neighborhoodservice.user.service;

import com.neighborhoodservice.user.dto.LoginRequest;
import com.neighborhoodservice.user.dto.RegisterKeycloakRequest;

public interface KeycloakService {

    String getAdminJwtToken();

    void createUser(String token, RegisterKeycloakRequest registerKeycloakRequest);

    String getUserIdByEmail(String email, String token);

    Object login(String adminJWT, LoginRequest loginRequest);

    void sendVerificationEmail(String adminJWT, String userId);

    void deleteUser(String adminJwt, String userId);
}
