package com.quick_park_assist.entityTest;
import static org.junit.jupiter.api.Assertions.*;

import com.quick_park_assist.entity.ParkingSpot;
import com.quick_park_assist.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

 class ParkingSpotTest {

    private ParkingSpot parkingSpot;
    private User user;

    @BeforeEach
    public void setUp() {
        parkingSpot = new ParkingSpot();
        user = new User();
        user.setId(1L);
        user.setFullName("Test User");
    }

    @Test
    public void testSetAndGetId() {
        parkingSpot.setId(1L);
        assertEquals(1L, parkingSpot.getId());
    }

    @Test
    public void testSetAndGetLocation() {
        String location = "Main Street";
        parkingSpot.setLocation(location);
        assertEquals(location, parkingSpot.getLocation());
    }

    @Test
    public void testSetAndGetSpotLocation() {
        String spotLocation = "Lot 42";
        parkingSpot.setSpotLocation(spotLocation);
        assertEquals(spotLocation, parkingSpot.getSpotLocation());
    }

    @Test
    public void testSetAndGetAvailability() {
        String availability = "Available";
        parkingSpot.setAvailability(availability);
        assertEquals(availability, parkingSpot.getAvailability());
    }

    @Test
    public void testSetAndGetPricePerHour() {
        double pricePerHour = 10.5;
        parkingSpot.setPricePerHour(pricePerHour);
        assertEquals(pricePerHour, parkingSpot.getPricePerHour());
    }

    @Test
    public void testSetAndGetSpotType() {
        String spotType = "Compact";
        parkingSpot.setSpotType(spotType);
        assertEquals(spotType, parkingSpot.getSpotType());
    }

    @Test
    public void testSetAndGetUser() {
        parkingSpot.setUser(user);
        assertEquals(user, parkingSpot.getUser());
    }

    @Test
    public void testSetAndGetAdditionalInstructions() {
        String instructions = "Park near the entrance.";
        parkingSpot.setAdditionalInstructions(instructions);
        assertEquals(instructions, parkingSpot.getAdditionalInstructions());
    }

    @Test
    public void testToString() {
        parkingSpot.setId(1L);
        parkingSpot.setLocation("Main Street");
        parkingSpot.setSpotLocation("Lot 42");
        parkingSpot.setAvailability("Available");
        parkingSpot.setPricePerHour(10.5);
        parkingSpot.setSpotType("Compact");
        parkingSpot.setUser(user);
        parkingSpot.setAdditionalInstructions("Park near the entrance.");
}

@Test
public void testDefaultValues() {
    assertNull(parkingSpot.getId());
    assertNull(parkingSpot.getLocation());
    assertNull(parkingSpot.getSpotLocation());
    assertNull(parkingSpot.getAvailability());
    assertEquals(0.0, parkingSpot.getPricePerHour());
    assertNull(parkingSpot.getSpotType());
    assertNull(parkingSpot.getUser());
    assertNull(parkingSpot.getAdditionalInstructions());
}
}
