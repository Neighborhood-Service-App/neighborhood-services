package com.neighborhoodservice.user.service.impl;

import com.neighborhoodservice.user.client.GoogleGeocodingClient;
import com.neighborhoodservice.user.service.GeocodingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
@Primary
public class GeocodingServiceImpl implements GeocodingService {

    private final GoogleGeocodingClient googleGeocodingClient;

    private String GOOGLE_API_KEY = System.getenv("GOOGLE_GEOCODING_API_KEY");

    @Override
    public Map<String, Double> getCoordinates(String address) {

        Map<String, Object> response = googleGeocodingClient.getCoordinates(address, GOOGLE_API_KEY);

        // Check if the response is valid
        if (response != null && "OK".equals(response.get("status"))) {
            List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
            if (!results.isEmpty()) {
                Map<String, Object> geometry = (Map<String, Object>) results.get(0).get("geometry");
                Map<String, Object> location = (Map<String, Object>) geometry.get("location");

                // Extract latitude and longitude
                Double lat = (Double) location.get("lat");
                Double lng = (Double) location.get("lng");

                log.info("Successfully retrieved location! Coordinates for address {} are lat: {}, lng: {}", address, lat, lng);
                return Map.of("lat", lat, "lng", lng);
            }
        }
        log.error("Failed to retrieve coordinates for address {}", address);
        throw new RuntimeException("Failed to retrieve coordinates for the given address.");
    }


}
