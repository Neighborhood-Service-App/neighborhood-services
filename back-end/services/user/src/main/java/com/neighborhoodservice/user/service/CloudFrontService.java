package com.neighborhoodservice.user.service;

import com.amazonaws.services.cloudfront.CloudFrontUrlSigner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.util.Base64;
import java.util.Date;


@Slf4j
@Service
public class CloudFrontService {


    @Value("${aws.cloudfront.key-pair-id}")
    private String keyPairId;

    @Value("${aws.cloudfront.domain-name}")
    private String cloudFrontDomain;

    @Value("${aws.cloudfront.private-key}")
    private String privateKeyStr;

    public String generateSignedUrl(String keyName, int expiryTimeInMinutes) {
        try {
            // Log the start of the process
            log.info("CloudFrontService: Generating signed URL for resource: {}", keyName);

            // Remove the PEM headers and footers from the private key string
            String cleanPemKey = privateKeyStr
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");  // Remove whitespace

            // Decode the Base64 private key
            byte[] decodedKey = Base64.getDecoder().decode(cleanPemKey);
            PrivateKey privateKey = getPrivateKey(decodedKey);

            // Set the expiration time for the signed URL
            long expirationTime = System.currentTimeMillis() + (expiryTimeInMinutes * 60 * 1000);

            // Create the signed URL
            String signedUrl = CloudFrontUrlSigner.getSignedURLWithCannedPolicy(
                    String.format("https://%s/%s", cloudFrontDomain, keyName),
                    keyPairId,
                    privateKey,
                    new Date(expirationTime)
            );

            // Log the successful generation of the URL
            log.info("Signed URL generated successfully: {}", signedUrl);

            return signedUrl;
        } catch (Exception e) {
            // Log the exception with more details
            log.error("Error generating signed URL", e);
            throw new RuntimeException("Error generating signed URL", e);
        }
    }

    private PrivateKey getPrivateKey(byte[] privateKeyBytes) throws Exception {
        try {
            return java.security.KeyFactory.getInstance("RSA")
                    .generatePrivate(new java.security.spec.PKCS8EncodedKeySpec(privateKeyBytes));
        } catch (Exception e) {
            log.error("Error getting private key", e);
            throw e;  // Rethrow the exception
        }
    }
}
