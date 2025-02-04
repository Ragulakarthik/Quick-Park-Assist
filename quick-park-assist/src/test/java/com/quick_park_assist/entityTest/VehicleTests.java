package com.quick_park_assist.entityTest;


import static org.junit.jupiter.api.Assertions.*;

import com.quick_park_assist.entity.User;
import com.quick_park_assist.entity.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class VehicleTest {

    private Vehicle vehicle;

    @BeforeEach
    void setUp() {
        vehicle = new Vehicle();
    }

    @Test
    void testDefaultValues() {
        // Verify default values
        assertNotNull(vehicle);
        assertFalse(vehicle.isEv());
    }

    @Test
    void testOnCreateSetsRegisteredAt() {
        // Call onCreate method
        vehicle.onCreate();

        // Verify registeredAt is set
        assertNotNull(vehicle.getRegisteredAt());
        assertTrue(vehicle.getRegisteredAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testSetAndGetUser() {
        // Create a dummy user
        User user = new User();
        user.setId(1L);

        // Set user to the vehicle
        vehicle.setUser(user);

        // Verify user is set correctly
        assertEquals(user, vehicle.getUser());
    }

    @Test
    void testSetAndGetVehicleNumber() {
        String vehicleNumber = "1234-AB";

        // Set vehicle number
        vehicle.setVehicleNumber(vehicleNumber);

        // Verify vehicle number is set correctly
        assertEquals(vehicleNumber, vehicle.getVehicleNumber());
    }

    @Test
    void testSetAndGetVehicleType() {
        String vehicleType = "Car";

        // Set vehicle type
        vehicle.setVehicleType(vehicleType);

        // Verify vehicle type is set correctly
        assertEquals(vehicleType, vehicle.getVehicleType());
    }

    @Test
    void testSetAndGetManufacturer() {
        String manufacturer = "Tesla";

        // Set manufacturer
        vehicle.setManufacturer(manufacturer);

        // Verify manufacturer is set correctly
        assertEquals(manufacturer, vehicle.getManufacturer());
    }

    @Test
    void testSetAndGetModel() {
        String model = "Model 3";

        // Set model
        vehicle.setModel(model);

        // Verify model is set correctly
        assertEquals(model, vehicle.getModel());
    }

    @Test
    void testSetAndGetColor() {
        String color = "Red";

        // Set color
        vehicle.setColor(color);

        // Verify color is set correctly
        assertEquals(color, vehicle.getColor());
    }

    @Test
    void testSetAndGetEv() {
        // Set EV flag
        vehicle.setEv(true);

        // Verify EV flag is set correctly
        assertTrue(vehicle.isEv());
    }

    @Test
    void testSetAndGetRegisteredAt() {
        LocalDateTime now = LocalDateTime.now();

        // Set registeredAt
        vehicle.setRegisteredAt(now);

        // Verify registeredAt is set correctly
        assertEquals(now, vehicle.getRegisteredAt());
    }

    @Test
    void testEqualsAndHashCode() {
        Vehicle vehicle1 = new Vehicle();
        Vehicle vehicle2 = vehicle1;

        vehicle1.setId(1L);
        vehicle2.setId(1L);

        // Verify equality based on ID
        assertEquals(vehicle1, vehicle2);
        assertEquals(vehicle1.hashCode(), vehicle2.hashCode());

        vehicle2 = new Vehicle();
        vehicle2.setId(2L);

        // Verify inequality based on different IDs
        assertNotEquals(vehicle1, vehicle2);
        assertNotEquals(vehicle1.hashCode(), vehicle2.hashCode());
    }
}

