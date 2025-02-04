package com.quick_park_assist.serviceImplTest;

import com.quick_park_assist.entity.ParkingSpot;
import com.quick_park_assist.repository.ParkingSpotRepository;
import com.quick_park_assist.serviceImpl.ParkingSpotPriceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ParkingSpotPriceServiceImplTest {

    @Mock
    private ParkingSpotRepository parkingSpotRepository;

    @InjectMocks
    private ParkingSpotPriceServiceImpl parkingSpotPriceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdatePrice_WithValidInputs() {
        // Arrange
        Long id = 1L;
        String location = "Level 1, Zone A";
        double pricePerHour = 100.0;
        String spotType = "Standard";
        String availability = "Available";
        String additionalInstructions = "Near Elevator";
        String accessibleSpot = "Accessible";

        ParkingSpot mockSpot = new ParkingSpot();
        mockSpot.setId(id);

        when(parkingSpotRepository.findById(id)).thenReturn(Optional.of(mockSpot));
        when(parkingSpotRepository.save(mockSpot)).thenReturn(mockSpot);

        // Act
        ParkingSpot updatedSpot = parkingSpotPriceService.updatePrice(id, location, pricePerHour, spotType, availability, additionalInstructions, accessibleSpot);

        // Assert
        assertNotNull(updatedSpot);
        assertEquals(location, updatedSpot.getLocation());
        assertEquals(pricePerHour, updatedSpot.getPricePerHour());
        assertEquals(spotType, updatedSpot.getSpotType());
        assertEquals(availability, updatedSpot.getAvailability());
        assertEquals(additionalInstructions, updatedSpot.getAdditionalInstructions());
        verify(parkingSpotRepository, times(1)).findById(id);
        verify(parkingSpotRepository, times(1)).save(mockSpot);
    }

    @Test
    void testUpdatePrice_WithNonexistentParkingSpot() {
        // Arrange
        Long id = 2L;
        String location = "Level 2, Zone B";
        double pricePerHour = 80.0;
        String spotType = "EV_SPOT";
        String availability = "Occupied";
        String additionalInstructions = "Near Exit";
        String accessibleSpot = "Not Accessible";

        when(parkingSpotRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                parkingSpotPriceService.updatePrice(id, location, pricePerHour, spotType, availability, additionalInstructions, accessibleSpot)
        );

        assertEquals("Parking spot not found", exception.getMessage());
        verify(parkingSpotRepository, times(1)).findById(id);
        verify(parkingSpotRepository, times(0)).save(any());
    }

    @Test
    void testUpdatePrice_WithZeroPricePerHour() {
        // Arrange
        Long id = 3L;
        String location = "Level 3, Zone C";
        double pricePerHour = 0.0; // Edge case
        String spotType = "Compact";
        String availability = "Available";
        String additionalInstructions = "Near Entrance";
        String accessibleSpot = "Accessible";

        ParkingSpot mockSpot = new ParkingSpot();
        mockSpot.setId(id);

        when(parkingSpotRepository.findById(id)).thenReturn(Optional.of(mockSpot));
        when(parkingSpotRepository.save(mockSpot)).thenReturn(mockSpot);

        // Act
        ParkingSpot updatedSpot = parkingSpotPriceService.updatePrice(id, location, pricePerHour, spotType, availability, additionalInstructions, accessibleSpot);

        // Assert
        assertNotNull(updatedSpot);
        assertEquals(pricePerHour, updatedSpot.getPricePerHour());
        verify(parkingSpotRepository, times(1)).findById(id);
        verify(parkingSpotRepository, times(1)).save(mockSpot);
    }

    @Test
    void testUpdatePrice_WithNullFields() {
        // Arrange
        Long id = 4L;
        double pricePerHour = 50.0;

        ParkingSpot mockSpot = new ParkingSpot();
        mockSpot.setId(id);

        when(parkingSpotRepository.findById(id)).thenReturn(Optional.of(mockSpot));
        when(parkingSpotRepository.save(mockSpot)).thenReturn(mockSpot);

        // Act
        ParkingSpot updatedSpot = parkingSpotPriceService.updatePrice(id, null, pricePerHour, null, null, null, null);

        // Assert
        assertNotNull(updatedSpot);
        assertEquals(pricePerHour, updatedSpot.getPricePerHour());
        assertNull(updatedSpot.getLocation());
        assertNull(updatedSpot.getSpotType());
        assertNull(updatedSpot.getAvailability());
        assertNull(updatedSpot.getAdditionalInstructions());
        verify(parkingSpotRepository, times(1)).findById(id);
        verify(parkingSpotRepository, times(1)).save(mockSpot);
    }
}
