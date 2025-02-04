package com.quick_park_assist.serviceImplTest;

import com.quick_park_assist.entity.ParkingSpot;
import com.quick_park_assist.repository.ParkingSpotRepository;
import com.quick_park_assist.serviceImpl.UpdateSpotServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UpdateSpotServiceImplTest {

    @Mock
    private ParkingSpotRepository parkingSpotRepository;

    @InjectMocks
    private UpdateSpotServiceImpl updateSpotServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test case for updating parking spot with valid data
    @Test
    void testUpdateParkingSpot_ValidData() {
        // Arrange
        Long spotId = 1L;
        String availability = "Available";
        Double pricePerHour = 20.0;
        String spotType = "Compact";
        String additionalInformation = "Near entrance";

        ParkingSpot parkingSpot = new ParkingSpot();
        when(parkingSpotRepository.findById(spotId)).thenReturn(Optional.of(parkingSpot));

        // Act
        boolean result = updateSpotServiceImpl.updateParkingSpot(spotId, availability, pricePerHour, spotType, additionalInformation);

        // Assert
        assertEquals(true, result);
        verify(parkingSpotRepository).save(parkingSpot);
        assertEquals(availability, parkingSpot.getAvailability());
        assertEquals(pricePerHour, parkingSpot.getPricePerHour());
        assertEquals(spotType, parkingSpot.getSpotType());
        assertEquals(additionalInformation, parkingSpot.getAdditionalInstructions());
    }

    // Test case for updating parking spot with invalid spot ID (non-existing spot)
    @Test
    void testUpdateParkingSpot_InvalidSpotId() {
        // Arrange
        Long spotId = 999L; // Invalid Spot ID
        String availability = "Available";
        Double pricePerHour = 20.0;
        String spotType = "Compact";
        String additionalInformation = "Near entrance";

        when(parkingSpotRepository.findById(spotId)).thenReturn(Optional.empty());

        // Act
        boolean result = updateSpotServiceImpl.updateParkingSpot(spotId, availability, pricePerHour, spotType, additionalInformation);

        // Assert
        assertEquals(false, result);
        verify(parkingSpotRepository, never()).save(any());
    }

    // Test case for updating parking spot with null availability
    @Test
    void testUpdateParkingSpot_NullAvailability() {
        // Arrange
        Long spotId = 1L;
        String availability = null; // Null availability
        Double pricePerHour = 20.0;
        String spotType = "Compact";
        String additionalInformation = "Near entrance";

        ParkingSpot parkingSpot = new ParkingSpot();
        when(parkingSpotRepository.findById(spotId)).thenReturn(Optional.of(parkingSpot));

        // Act
        boolean result = updateSpotServiceImpl.updateParkingSpot(spotId, availability, pricePerHour, spotType, additionalInformation);

        // Assert
        assertEquals(true, result);
        verify(parkingSpotRepository).save(parkingSpot);
        assertEquals(availability, parkingSpot.getAvailability());
    }

    // Test case for updating parking spot with invalid price per hour
    @Test
    void testUpdateParkingSpot_InvalidPricePerHour() {
        // Arrange
        Long spotId = 1L;
        String availability = "Available";
        Double pricePerHour = -10.0; // Invalid price
        String spotType = "Compact";
        String additionalInformation = "Near entrance";

        ParkingSpot parkingSpot = new ParkingSpot();
        when(parkingSpotRepository.findById(spotId)).thenReturn(Optional.of(parkingSpot));

        // Act
        boolean result = updateSpotServiceImpl.updateParkingSpot(spotId, availability, pricePerHour, spotType, additionalInformation);

        // Assert
        assertEquals(true, result); // You may want to handle price validation here in the service logic
        verify(parkingSpotRepository).save(parkingSpot);
        assertEquals(pricePerHour, parkingSpot.getPricePerHour());
    }

    // Test case for updating parking spot with null spot type
    @Test
    void testUpdateParkingSpot_NullSpotType() {
        // Arrange
        Long spotId = 1L;
        String availability = "Available";
        Double pricePerHour = 20.0;
        String spotType = null; // Null spot type
        String additionalInformation = "Near entrance";

        ParkingSpot parkingSpot = new ParkingSpot();
        when(parkingSpotRepository.findById(spotId)).thenReturn(Optional.of(parkingSpot));

        // Act
        boolean result = updateSpotServiceImpl.updateParkingSpot(spotId, availability, pricePerHour, spotType, additionalInformation);

        // Assert
        assertEquals(true, result);
        verify(parkingSpotRepository).save(parkingSpot);
        assertEquals(spotType, parkingSpot.getSpotType());
    }

    // Test case for updating parking spot with null additional instructions
    @Test
    void testUpdateParkingSpot_NullAdditionalInstructions() {
        // Arrange
        Long spotId = 1L;
        String availability = "Available";
        Double pricePerHour = 20.0;
        String spotType = "Compact";
        String additionalInformation = null; // Null additional instructions

        ParkingSpot parkingSpot = new ParkingSpot();
        when(parkingSpotRepository.findById(spotId)).thenReturn(Optional.of(parkingSpot));

        // Act
        boolean result = updateSpotServiceImpl.updateParkingSpot(spotId, availability, pricePerHour, spotType, additionalInformation);

        // Assert
        assertEquals(true, result);
        verify(parkingSpotRepository).save(parkingSpot);
        assertEquals(additionalInformation, parkingSpot.getAdditionalInstructions());
    }
}
