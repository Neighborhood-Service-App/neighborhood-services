package com.neighborhoodservice.user.service;

import com.neighborhoodservice.user.dto.RegisterKeycloakRequest;

public interface KeycloakService {

    public String getAdminJwtToken(String clientId, String username, String password);

    public void createUser(String token, RegisterKeycloakRequest registerKeycloakRequest);

    public String getUserIdByEmail(String email, String token);

}
