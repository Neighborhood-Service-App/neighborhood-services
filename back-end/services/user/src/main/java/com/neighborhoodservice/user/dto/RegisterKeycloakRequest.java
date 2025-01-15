package com.neighborhoodservice.user.dto;

import java.util.List;

public record RegisterKeycloakRequest(
        boolean enabled,
        String firstName,
        String lastName,
        String email,
        List<Credentials> credentials
) {

    public static class Credentials {
        String type = "password";
        String value;
        boolean temporary = false;

        // Constructor, getters, and setters
        public Credentials(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }
}
