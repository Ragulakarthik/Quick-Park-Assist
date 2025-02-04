package com.quick_park_assist.serviceImpl;


import com.quick_park_assist.entity.Reservation;
import com.quick_park_assist.repository.ReservationRepository;
import com.quick_park_assist.service.IReservationService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationServiceImpl implements IReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    // Get all reservations
    @Override
    public List<Reservation> getReservationsByUserId(Long userId) {
        return reservationRepository.findByUserId(userId);
    }
    @Override
    @Transactional
    public boolean updateSpotDetails(Long bookingId, Date startTime, String vehicleNumber) {
        Optional<Reservation> bookingOptional =reservationRepository.findById(bookingId);

        if (bookingOptional.isPresent()) {
           Reservation reservation = bookingOptional.get();
            reservation.setReservationTime(startTime);
            reservation.setVehicleNumber(vehicleNumber); // Update vehicle number
            reservationRepository.save(reservation);
            return true;
        }
        return false;
    }


    // Get reservation by ID
    public Reservation getReservationById(Long id) {
        Optional<Reservation> reservation = reservationRepository.findById(id);
        return reservation.orElse(null);
    }

    // Add a new reservation
    public void addReservation(Reservation reservation) {
        reservationRepository.save(reservation);
    }


    // Delete a reservation
    public boolean deleteReservationById(Long id) {
        Optional<Reservation> bookedReservation = reservationRepository.findById(id);
        if (bookedReservation.isPresent()) {
            Reservation reservation = bookedReservation.get();
            reservationRepository.delete(reservation);
            return true;// Delete the reservation from the database
        }
        return false;
    }
    public boolean isTimeSlotAvailable(Date reservationTime, String vehicleNumber) {
        // Logic to check for overlapping reservations
        return reservationRepository.isTimeSlotAvailable(reservationTime, vehicleNumber);
    }

}