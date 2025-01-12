package com.neighborhoodservice.user.dto;

import java.util.UUID;

public record KeyCloakUserResponse(
         String id,
         String email,
         boolean enabled
) {
    public UUID getId() {
        return UUID.fromString(id);
    }
}
