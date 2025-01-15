package com.neighborhoodservice.user.service;

public interface CloudFrontService {
    String generateSignedUrl(String keyName, int expiryTimeInMinutes);
}
