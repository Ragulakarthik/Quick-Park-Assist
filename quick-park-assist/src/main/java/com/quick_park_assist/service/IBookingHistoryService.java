package com.quick_park_assist.service;

import com.quick_park_assist.entity.BookingSpot;

import java.util.List;

public interface IBookingHistoryService {
     List<BookingSpot> getBookingsByuserID(Long UserID);
}
