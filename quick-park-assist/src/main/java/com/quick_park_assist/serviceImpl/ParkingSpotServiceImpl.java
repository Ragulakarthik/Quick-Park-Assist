package com.quick_park_assist.serviceImpl;

import com.quick_park_assist.entity.ParkingSpot;
import com.quick_park_assist.repository.ParkingSpotRepository;
import com.quick_park_assist.service.IParkingSpotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParkingSpotServiceImpl implements IParkingSpotService {

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    /**
     * Fetches all parking spots with "available" availability (ignoring case).
     *
     * @return List of available ParkingSpot objects
     */
    public List<ParkingSpot> getAllAvailableSpots(String searchQuery) {
        return parkingSpotRepository.findByAvailabilityIgnoreCaseAndSpotLocationContainingIgnoreCaseOrLocationContainingIgnoreCaseAndSpotTypeNot("available",searchQuery,searchQuery,"EV_SPOT");
    }
}
