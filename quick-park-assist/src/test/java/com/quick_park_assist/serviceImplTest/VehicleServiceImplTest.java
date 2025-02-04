package com.quick_park_assist.serviceImplTest;


import com.quick_park_assist.dto.VehicleDTO;
import com.quick_park_assist.entity.User;
import com.quick_park_assist.entity.Vehicle;
import com.quick_park_assist.repository.UserRepository;
import com.quick_park_assist.repository.VehicleRepository;
import com.quick_park_assist.serviceImpl.VehicleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VehicleServiceImplTest {

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetVehiclesByUserId() {
        // Arrange
        Long userId = 1L;
        List<Vehicle> vehicles = List.of(new Vehicle(), new Vehicle());
        when(vehicleRepository.findByUserId(userId)).thenReturn(vehicles);

        // Act
        List<Vehicle> result = vehicleService.getVehiclesByUserId(userId);

        // Assert
        assertEquals(vehicles, result);
        verify(vehicleRepository, times(1)).findByUserId(userId);
    }

    @Test
    void testGetVehicleByIdAndUserId_Success() {
        // Arrange
        Long vehicleId = 1L;
        Long userId = 2L;
        Vehicle vehicle = new Vehicle();
        when(vehicleRepository.findByIdAndUserId(vehicleId, userId)).thenReturn(Optional.of(vehicle));

        // Act
        Vehicle result = vehicleService.getVehicleByIdAndUserId(vehicleId, userId);

        // Assert
        assertEquals(vehicle, result);
        verify(vehicleRepository, times(1)).findByIdAndUserId(vehicleId, userId);
    }

    @Test
    void testGetVehicleByIdAndUserId_NotFound() {
        // Arrange
        Long vehicleId = 1L;
        Long userId = 2L;
        when(vehicleRepository.findByIdAndUserId(vehicleId, userId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> vehicleService.getVehicleByIdAndUserId(vehicleId, userId));
        assertEquals("Vehicle not found or unauthorized", exception.getMessage());
    }

    @Test
    void testAddVehicle_Success() {
        // Arrange
        Long userId = 1L;
        VehicleDTO vehicleDTO = new VehicleDTO();
        vehicleDTO.setVehicleNumber("AB1234");
        vehicleDTO.setVehicleType("Car");

        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Vehicle savedVehicle = new Vehicle();
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(savedVehicle);

        // Act
        Vehicle result = vehicleService.addVehicle(userId, vehicleDTO);

        // Assert
        assertEquals(savedVehicle, result);

        // Verify the repository interactions
        verify(userRepository, times(1)).findById(userId);
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    void testAddVehicle_UserNotFound() {
        // Arrange
        Long userId = 1L;
        VehicleDTO vehicleDTO = new VehicleDTO();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> vehicleService.addVehicle(userId, vehicleDTO));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testUpdateVehicle_Success() {
        // Arrange
        Long vehicleId = 1L;
        Long userId = 1L;
        VehicleDTO vehicleDTO = new VehicleDTO();
        vehicleDTO.setVehicleNumber("XY5678");

        Vehicle vehicle = new Vehicle();
        when(vehicleRepository.findByIdAndUserId(vehicleId, userId)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);

        // Act
        Vehicle result = vehicleService.updateVehicle(vehicleId, userId, vehicleDTO);

        // Assert
        assertEquals(vehicle, result);
        assertEquals("XY5678", vehicle.getVehicleNumber());
        verify(vehicleRepository, times(1)).save(vehicle);
    }

    @Test
    void testUpdateVehicle_NotFound() {
        // Arrange
        Long vehicleId = 1L;
        Long userId = 1L;
        VehicleDTO vehicleDTO = new VehicleDTO();
        when(vehicleRepository.findByIdAndUserId(vehicleId, userId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> vehicleService.updateVehicle(vehicleId, userId, vehicleDTO));
        assertEquals("Vehicle not found or unauthorized", exception.getMessage());
    }

    @Test
    void testDeleteVehicle_Success() {
        // Arrange
        Long vehicleId = 1L;
        Long userId = 1L;
        Vehicle vehicle = new Vehicle();
        when(vehicleRepository.findByIdAndUserId(vehicleId, userId)).thenReturn(Optional.of(vehicle));

        // Act
        vehicleService.deleteVehicle(vehicleId, userId);

        // Assert
        verify(vehicleRepository, times(1)).delete(vehicle);
    }

    @Test
    void testDeleteVehicle_NotFound() {
        // Arrange
        Long vehicleId = 1L;
        Long userId = 1L;
        when(vehicleRepository.findByIdAndUserId(vehicleId, userId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> vehicleService.deleteVehicle(vehicleId, userId));
        assertEquals("Vehicle not found or unauthorized", exception.getMessage());
    }
}

