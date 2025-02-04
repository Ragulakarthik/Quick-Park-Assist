package com.quick_park_assist.repository;



import com.quick_park_assist.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    boolean existsByChargingStationAndReservationTime(String chargingStation, LocalDateTime reservationTime);
    // Find reservation by vehicle number
    Reservation findByVehicleNumberAndId(String vehicleNumber,Long Id);
    List<Reservation> findByUserId(Long userId);
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN false ELSE true END " +
            "FROM Reservation r WHERE r.vehicleNumber = :vehicleNumber AND r.reservationTime = :reservationTime")
    boolean isTimeSlotAvailable(@Param("reservationTime") Date reservationTime, @Param("vehicleNumber") String vehicleNumber);
 /*   @Query("SELECT COUNT(DISTINCT r.user.id) FROM Reservation r WHERE r.parkingSpot.user.id = :userId")
    int countUniqueCustomersByOwner(Long userId);
*/
}
