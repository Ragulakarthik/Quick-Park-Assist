package com.quick_park_assist.serviceImpl;

import com.quick_park_assist.repository.BookingSpotRepository;
import com.quick_park_assist.repository.VehicleRepository;
import com.quick_park_assist.service.IStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class StatsServiceImpl implements IStatsService {

    private  BookingSpotRepository bookingSpotRepository;
    private VehicleRepository vehicleRepository;


    @Autowired
    public StatsServiceImpl(BookingSpotRepository bookingSpotRepository,VehicleRepository vehicleRepository) {
        this.bookingSpotRepository = bookingSpotRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @Override

    public Map<String, Object> getStatsForUser(Long userId) {
            Map<String, Object> stats = new HashMap<>();
            stats.put("availableSpots", bookingSpotRepository.countByUserId(userId));
            stats.put("totalHours", bookingSpotRepository.sumDurationByUserId(userId));
            stats.put("amountSpent", bookingSpotRepository.sumEstimatedPriceByUserId(userId));
            stats.put("activeBookings", vehicleRepository.countByUserId(userId)); // Added Vehicle Count
            return stats;
    }

    @Override
    public List<Map<String, Object>> getRecentActivity(Long userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return bookingSpotRepository.findRecentActivityByUserId(userId, pageable).getContent();

    }
}
