package com.quick_park_assist.DTOTest;

import com.quick_park_assist.dto.VehicleDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VehicleDTOTest {

    @Test
    void testGetAndSetVehicleNumber() {
        VehicleDTO vehicle = new VehicleDTO();
        String vehicleNumber = "MH-12-AB-1234";

        vehicle.setVehicleNumber(vehicleNumber);
        assertEquals(vehicleNumber, vehicle.getVehicleNumber(), "Vehicle number should match the set value");
    }

    @Test
    void testGetAndSetVehicleType() {
        VehicleDTO vehicle = new VehicleDTO();
        String vehicleType = "Car";

        vehicle.setVehicleType(vehicleType);
        assertEquals(vehicleType, vehicle.getVehicleType(), "Vehicle type should match the set value");
    }

    @Test
    void testGetAndSetManufacturer() {
        VehicleDTO vehicle = new VehicleDTO();
        String manufacturer = "Toyota";

        vehicle.setManufacturer(manufacturer);
        assertEquals(manufacturer, vehicle.getManufacturer(), "Manufacturer should match the set value");
    }

    @Test
    void testGetAndSetModel() {
        VehicleDTO vehicle = new VehicleDTO();
        String model = "Corolla";

        vehicle.setModel(model);
        assertEquals(model, vehicle.getModel(), "Model should match the set value");
    }

    @Test
    void testGetAndSetColor() {
        VehicleDTO vehicle = new VehicleDTO();
        String color = "Red";

        vehicle.setColor(color);
        assertEquals(color, vehicle.getColor(), "Color should match the set value");
    }

    @Test
    void testGetAndSetEv() {
        VehicleDTO vehicle = new VehicleDTO();
        boolean ev = true;

        vehicle.setEv(ev);
        assertTrue(vehicle.isEv(), "EV flag should be true");

        vehicle.setEv(false);
        assertFalse(vehicle.isEv(), "EV flag should be false");
    }

    @Test
    void testDefaultValues() {
        VehicleDTO vehicle = new VehicleDTO();

        assertNull(vehicle.getVehicleNumber(), "Default vehicle number should be null");
        assertNull(vehicle.getVehicleType(), "Default vehicle type should be null");
        assertNull(vehicle.getManufacturer(), "Default manufacturer should be null");
        assertNull(vehicle.getModel(), "Default model should be null");
        assertNull(vehicle.getColor(), "Default color should be null");
        assertFalse(vehicle.isEv(), "Default EV flag should be false");
    }
}
