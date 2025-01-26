package com.quick_park_assist.serviceImpl;

import java.util.List;

import com.quick_park_assist.enums.BookingSpotStatus;
import com.quick_park_assist.service.IViewBookingByMobileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.quick_park_assist.entity.BookingSpot;
import com.quick_park_assist.repository.BookingSpotRepository;

@Service
public class ViewBookingByMobileServiceImpl implements IViewBookingByMobileService {

    @Autowired
    private BookingSpotRepository bookingSpotRepository;

    @Override
    public List<BookingSpot> getConfirmedBookingsByUserID(Long UserID) {
       return  bookingSpotRepository.findByUserIDAndBookingSpotStatus(UserID, BookingSpotStatus.CONFIRMED);
    }
}