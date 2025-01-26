package com.quick_park_assist.serviceImpl;

import com.quick_park_assist.entity.BookingSpot;
import com.quick_park_assist.entity.ParkingSpot;
import com.quick_park_assist.enums.BookingSpotStatus;
import com.quick_park_assist.repository.BookingSpotRepository;
import com.quick_park_assist.repository.ParkingSpotRepository;
import com.quick_park_assist.service.IModifySpotService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ModifySpotServiceImpl implements IModifySpotService {

    @Autowired
    private BookingSpotRepository bookingSpotRepository;
    @Autowired
    private ParkingSpotRepository parkingSpotRepository;
    @Override
    public List<BookingSpot> getConfirmedBookings(Long UserID) {

        return bookingSpotRepository.findByUserIDAndBookingSpotStatus(UserID, BookingSpotStatus.CONFIRMED);
    }
    @Override
    public boolean updateSpotDetails(@PathVariable Long bookingId, @RequestBody Date startTime, @RequestBody Double duration,@PathVariable Long spotID) {
        // Find the booking by Spot ID
        Optional<BookingSpot> bookingOptional = bookingSpotRepository.findById(bookingId);
        if (bookingOptional.isPresent()) {
            BookingSpot bookingSpot = bookingOptional.get();
            bookingSpot.setStartTime(startTime); // Assumes `startTime` is passed as an ISO string
            bookingSpot.setDuration(duration);
            Optional<ParkingSpot> parkingspot =parkingSpotRepository.findById(spotID);
            if(parkingspot.isPresent()) {
                ParkingSpot spot = parkingspot.get();
                Double spotPrice = spot.getPricePerHour();
                Double newPrice = spotPrice * duration;
                bookingSpot.setEstimatedPrice(newPrice);

                // Calculate and set endTime based on the duration
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startTime);
                int durationMinutes = (int) (duration * 60);
                calendar.add(Calendar.MINUTE, durationMinutes);
                Date endTime = calendar.getTime(); // Convert duration to minutes
                bookingSpot.setEndTime(endTime);
                bookingSpotRepository.save(bookingSpot);
                return true;
            }
            return false;
        }
        return false;
    }

}
