package com.quick_park_assist.service;
import com.quick_park_assist.entity.BookingSpot;
import jakarta.transaction.Transactional;

import java.util.List;

public interface ICancelSpotService {
    @Transactional
    boolean cancelBooking(Long bookingId);
    @Transactional
    List<BookingSpot> getConfirmedBookingsByUserID(Long UserID);
}
