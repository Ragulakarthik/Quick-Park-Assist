// VehicleRepository.java in repository package
package com.quick_park_assist.repository;

import com.quick_park_assist.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END " +
            "FROM Vehicle v WHERE v.vehicleNumber = :vehicleNumber AND v.user.id = :userId AND v.ev = true")
    boolean existsByVehicleNumberAndUserIdAndEvTrue(String vehicleNumber, Long userId);

    // Query to fetch EV vehicles for a specific user
    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END " +
            "FROM Vehicle v WHERE v.user.id = :userId AND v.ev = true")
    boolean existsElectricVehicleByUserId(Long userId);

    boolean existsVehicleByVehicleNumber(String vehicleNumber);

    List<Vehicle> findByUserId(Long userId);
    Optional<Vehicle> findByIdAndUserId(Long id, Long userId);
    @Query("SELECT COUNT(v) FROM Vehicle v WHERE v.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);

    boolean existsVehicleByVehicleNumberAndUserIdAndEvTrue(String vehicleNum, Long userId);

    Optional<Vehicle> findByVehicleNumber(String vehicleNumber);

    @Query("SELECT vehicleNumber FROM Vehicle v WHERE v.user.id = :userId AND v.ev = true ")
    List<String> findEVVehicles(Long userId);
}