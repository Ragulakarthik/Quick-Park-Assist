package com.quick_park_assist.serviceImpl;

import com.quick_park_assist.entity.BookingSpot;
import com.quick_park_assist.repository.BookingSpotRepository;
import com.quick_park_assist.service.IBookingHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingHistoryServiceImpl implements IBookingHistoryService {
    @Autowired
    private BookingSpotRepository bookingSpotRepository;
    @Override
    public List<BookingSpot> getBookingsByuserID(Long UserID) {
        return bookingSpotRepository.findBookingsByUserId(UserID);
    }
}
