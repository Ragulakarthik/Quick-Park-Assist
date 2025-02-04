package com.quick_park_assist.repository;

import com.quick_park_assist.entity.ParkingSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long> {
    @Query("SELECT COUNT(p) FROM ParkingSpot p WHERE p.user.id = :userId")
    int countTotalSpotsByOwner(Long userId);

    @Query("SELECT COUNT(p) FROM ParkingSpot p WHERE p.user.id = :userId AND p.spotType = 'EV_SPOT'")
    int countEvSpotsByOwner(Long userId);

    // 1. Find by location (case-insensitive) and spotType not equal to "EV_SPOT"
    List<ParkingSpot> findByLocationContainingIgnoreCaseAndSpotTypeNot(String location, String spotType);

    // 2. Find by location, availability, and spotType not equal to "EV_SPOT"
    List<ParkingSpot> findByLocationContainingIgnoreCaseAndAvailabilityIgnoreCaseAndSpotTypeNot(
            String location, String availability, String spotType);

    // 3. Find by availability and spotLocation or location (both case-insensitive), and spotType not equal to "EV_SPOT"
    List<ParkingSpot> findByAvailabilityIgnoreCaseAndSpotLocationContainingIgnoreCaseOrLocationContainingIgnoreCaseAndSpotTypeNot(
            String availability, String spotLocation, String location, String spotType);
    boolean existsParkingSpotBySpotLocationIgnoreCaseAndLocationIgnoreCase(String spotLocation,String Location);
    List<ParkingSpot> findByUserId(Long userId);
    List<ParkingSpot> findByAvailabilityIgnoreCaseAndSpotLocationContainingIgnoreCaseOrLocationContainingIgnoreCaseAndSpotType(
            String availability, String spotLocation, String location, String spotType);

    List<ParkingSpot> findBySpotType(String evSpot);
}