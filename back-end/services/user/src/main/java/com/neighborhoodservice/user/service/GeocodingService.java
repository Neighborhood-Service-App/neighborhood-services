package com.neighborhoodservice.user.service;

import java.util.Map;

public interface GeocodingService {
    Map<String, Double> getCoordinates(String address);
}
