package com.quick_park_assist.service;

import com.quick_park_assist.entity.Reservation;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Service
public interface IReservationService {
     List<Reservation> getReservationsByUserId(Long userId);
     boolean updateSpotDetails(Long bookingId, Date startTime, String vehicleNumber);
     void addReservation(Reservation reservation);
     boolean deleteReservationById(Long id);
     boolean isTimeSlotAvailable(Date reservationTime, String vehicleNumber);
}
