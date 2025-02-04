package com.quick_park_assist.serviceImpl;

import com.quick_park_assist.entity.ParkingSpot;
import com.quick_park_assist.repository.ParkingSpotRepository;
import com.quick_park_assist.service.IUpdateParkingSpotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
@Service
public class UpdateSpotServiceImpl implements IUpdateParkingSpotService {
    @Autowired
    ParkingSpotRepository parkingSpotRepository;

    @Override
    public List<ParkingSpot> getParkingSpotsForLoggedInUser(Long userId) {
        // Get the logged-in
        // user's username from the session
        return parkingSpotRepository.findByUserId(userId);
    }
    @Override
    public boolean updateParkingSpot(
            @PathVariable Long spotId,
            @RequestBody String availability,
            @RequestBody Double pricePerHour,
            @RequestBody String spotType,
            @RequestBody String additionalInstructions) {
        // Fetch the parking spot by ID
        ParkingSpot parkingSpot = parkingSpotRepository.findById(spotId)
                .orElse(null);
        if(parkingSpot != null) {
            // Update the fields
            parkingSpot.setSpotType(spotType);
            parkingSpot.setAvailability(availability);
            parkingSpot.setAdditionalInstructions(additionalInstructions);
            parkingSpot.setPricePerHour(pricePerHour);

            // Save the updated entity
            parkingSpotRepository.save(parkingSpot);
            return true;
        }
        return false;
    }
}
