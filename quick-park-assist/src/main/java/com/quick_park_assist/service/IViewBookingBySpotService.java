package com.quick_park_assist.service;

import com.quick_park_assist.entity.BookingSpot;
import java.util.List;

public interface IViewBookingBySpotService {
    List<BookingSpot> getBookingsBySpotLocation(Long userID,String spotLocation);
}
