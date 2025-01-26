// VehicleServiceImpl.java in service package
package com.quick_park_assist.serviceImpl;

import com.quick_park_assist.dto.VehicleDTO;
import com.quick_park_assist.entity.User;
import com.quick_park_assist.entity.Vehicle;
import com.quick_park_assist.repository.UserRepository;
import com.quick_park_assist.repository.VehicleRepository;
import com.quick_park_assist.service.IVehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VehicleServiceImpl implements IVehicleService {

    public static final String VEHICLE_NOT_FOUND_OR_UNAUTHORIZED = "Vehicle not found or unauthorized";
    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Vehicle> getVehiclesByUserId(Long userId) {
        return vehicleRepository.findByUserId(userId);
    }

    @Override
    public Vehicle getVehicleByIdAndUserId(Long vehicleId, Long userId) {
        return vehicleRepository.findByIdAndUserId(vehicleId, userId)
                .orElseThrow(() -> new RuntimeException(VEHICLE_NOT_FOUND_OR_UNAUTHORIZED));
    }


    @Override
    public Vehicle addVehicle(Long userId, VehicleDTO vehicleDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Vehicle vehicle = new Vehicle();
        vehicle.setUser(user);
        updateVehicleFromDTO(vehicle, vehicleDTO);

        return vehicleRepository.save(vehicle);
    }

    @Override
    @Transactional
    public Vehicle updateVehicle(Long vehicleId, Long userId, VehicleDTO vehicleDTO) {
        Vehicle vehicle = vehicleRepository.findByIdAndUserId(vehicleId, userId)
                .orElseThrow(() -> new RuntimeException(VEHICLE_NOT_FOUND_OR_UNAUTHORIZED));

        updateVehicleFromDTO(vehicle, vehicleDTO);
        return vehicleRepository.save(vehicle);
    }

    @Override
    @Transactional
    public void deleteVehicle(Long vehicleId, Long userId) {
        Vehicle vehicle = vehicleRepository.findByIdAndUserId(vehicleId, userId)
                .orElseThrow(() -> new RuntimeException(VEHICLE_NOT_FOUND_OR_UNAUTHORIZED));
        vehicleRepository.delete(vehicle);
    }

    // Helper method to update Vehicle from DTO
    private void updateVehicleFromDTO(Vehicle vehicle, VehicleDTO vehicleDTO) {
        vehicle.setVehicleNumber(vehicleDTO.getVehicleNumber());
        vehicle.setVehicleType(vehicleDTO.getVehicleType());
        vehicle.setManufacturer(vehicleDTO.getManufacturer());
        vehicle.setModel(vehicleDTO.getModel());
        vehicle.setColor(vehicleDTO.getColor());
        vehicle.setEv(vehicleDTO.isEv());
    }
}