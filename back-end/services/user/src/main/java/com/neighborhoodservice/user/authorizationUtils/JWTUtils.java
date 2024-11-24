package com.neighborhoodservice.user.authorizationUtils;

import com.neighborhoodservice.user.exception.AuthorizationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class JWTUtils {

    private static PublicKey getPublicKey() throws Exception {
        // Get the public key from the environment variable
        String publicKeyPem = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsXguwWMRONpFl6YnI12KbFSQa9TiPZ30c+WM34QvefxoCR8XuxrQAhLwh8N1osYB1ueq8im8rMeTxEwpC4ndNjhLUpV7kHDZC08+AtDMoEcsF+ErRGl2ltAqJetdNBdg31BJeQo5IOdfyEjZBt7F7K3B0P1B3WV9uJg8f8p6e7XQjpleBSufcn+EAPaFsl4ObYccXE/FjnU2+DwiwlxNEUGxlKU/PhsVhkcKVwycvD3jhlqpcLQ0cQvFvaGbjpnhsOpU0Kf+1RqQbe7v/yKNjIg6KVv0v+DKBiHOBnhvtAoFubVO9zgtT5VJipLoRDw6MUuOW0Eu31Qf3ObnVKnijwIDAQAB";


        // Decode the base64 encoded public key
        byte[] decoded = Base64.getDecoder().decode(publicKeyPem);

        // Convert the decoded key to a PublicKey object
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    // Decode and verify JWT
    public static Claims decodeAndVerifyJwt(String jwt) throws Exception {
        PublicKey publicKey = getPublicKey();

        Claims claims = Jwts
                .parser()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(jwt)
                .getBody();

        return claims;
    }


    // Extract resource roles for a specific client (e.g., "account", "realm-management") from JWT claims
    public List<String> extractResourceRoles(Claims claims, String clientId) {
        Map<String, Map<String, Object>> resourceAccess = (Map<String, Map<String, Object>>) claims.get("resource_access");
        if (resourceAccess != null && resourceAccess.containsKey(clientId)) {
            Map<String, Object> clientRoles = resourceAccess.get(clientId);
            return (List<String>) clientRoles.get("roles");
        }
        return null;
    }

    public static UUID getUserIdFromToken(String token) throws Exception {
        Claims claims = decodeAndVerifyJwt(formatToken(token));
        return UUID.fromString(claims.getSubject());
    }

    // Check if the user has the 'admin' role in realm or resource access
    public boolean hasAdminRole(String token) throws Exception {

        Claims claims = decodeAndVerifyJwt(formatToken(token));
        // Check 'realm_access' roles
        List<String> realmManagementRoles = extractResourceRoles(claims, "realm-management");
        log.info("User realm-management roles: {}", realmManagementRoles);
        if (realmManagementRoles != null && realmManagementRoles.contains("realm-admin")) {
            return true;
        }

        log.warn("User does not have admin role!");
        throw new AuthorizationException("User does not have admin role!");
    }


    private static String formatToken(String token) {
        return token.substring(7);
    }


}