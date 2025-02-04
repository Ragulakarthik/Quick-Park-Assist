package com.quick_park_assist.serviceImpl;

import com.quick_park_assist.entity.BookingSpot;
import com.quick_park_assist.repository.BookingSpotRepository;
import com.quick_park_assist.service.IViewBookingBySpotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ViewBookingBySpotServiceImpl implements IViewBookingBySpotService {

    @Autowired
    private BookingSpotRepository bookingSpotRepository;

    @Override
    public List<BookingSpot> getBookingsBySpotLocation(Long userID,String spotLocation) {
        return bookingSpotRepository.getBookingsBySpotLocationAndUserId(userID,spotLocation);
    }
}
