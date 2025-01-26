package com.quick_park_assist.service;

import com.quick_park_assist.entity.BookingSpot;
import com.quick_park_assist.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
@Service
public interface IModifySpotService {
    boolean updateSpotDetails(Long BookingId, Date startTime, Double duration, Long spotID);
    List<BookingSpot> getConfirmedBookings(Long UserID);
}
