package com.neighborhoodservice.user.service;

import com.neighborhoodservice.user.dto.RegisterKeycloakRequest;

public interface KeycloakService {

    String getAdminJwtToken(String clientId, String username, String password);

    void createUser(String token, RegisterKeycloakRequest registerKeycloakRequest);

    String getUserIdByEmail(String email, String token);

}
