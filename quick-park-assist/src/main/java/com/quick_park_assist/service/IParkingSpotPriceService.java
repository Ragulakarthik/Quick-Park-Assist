package com.quick_park_assist.service;

import com.quick_park_assist.entity.ParkingSpot;
import org.springframework.stereotype.Service;

@Service
public interface IParkingSpotPriceService {
    ParkingSpot updatePrice(Long id, String location, double pricePerHour, String spotType, String availability, String additionalInstructions, String accessibleSpot);
}
