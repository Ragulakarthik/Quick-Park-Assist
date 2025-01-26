package com.quick_park_assist.service;

import com.quick_park_assist.entity.BookingSpot;

import java.util.Date;

public interface IBookingSpotService {
    void saveBookingSpot(BookingSpot bookingSpot);
    boolean checkIfPreviouslyBooked(Long userId, Long spotId, Date startTime);
}