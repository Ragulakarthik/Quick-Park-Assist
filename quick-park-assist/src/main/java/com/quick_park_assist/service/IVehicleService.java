package com.quick_park_assist.service;

import com.quick_park_assist.dto.VehicleDTO;
import com.quick_park_assist.entity.Vehicle;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IVehicleService {
    List<Vehicle> getVehiclesByUserId(Long userId);
    Vehicle addVehicle(Long userId, VehicleDTO vehicleDTO);
    void deleteVehicle(Long vehicleId, Long userId);
    Vehicle getVehicleByIdAndUserId(Long vehicleId, Long userId);
    Vehicle updateVehicle(Long vehicleId, Long userId, VehicleDTO vehicleDTO);

}



