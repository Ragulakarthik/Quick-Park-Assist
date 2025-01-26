package com.quick_park_assist.service;

import com.quick_park_assist.dto.ParkingSpotUpdateDTO;
import com.quick_park_assist.entity.ParkingSpot;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface IUpdateParkingSpotService {
    List<ParkingSpot> getParkingSpotsForLoggedInUser(Long userId);
    boolean updateParkingSpot(Long spotId, String availability,Double pricePerHour, String spotType,String additionalInformation);


}
