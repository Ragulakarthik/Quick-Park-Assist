package com.quick_park_assist.service;

import com.quick_park_assist.entity.ParkingSpot;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface IParkingSpotService {
    List<ParkingSpot> getAllAvailableSpots(String searchQuery);
}
