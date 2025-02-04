package com.quick_park_assist.serviceImplTest;

import com.quick_park_assist.repository.BookingSpotRepository;
import com.quick_park_assist.repository.VehicleRepository;
import com.quick_park_assist.repository.ParkingSpotRepository;
import com.quick_park_assist.repository.ReservationRepository;
import com.quick_park_assist.serviceImpl.StatsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class StatsServiceImplTest {

    @Mock
    private BookingSpotRepository bookingSpotRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private ParkingSpotRepository parkingSpotRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private StatsServiceImpl statsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetStatsForUser() {
        // Arrange
        Long userId = 1L;
        when(bookingSpotRepository.countByUserId(userId)).thenReturn(5L);
        when(bookingSpotRepository.sumDurationByUserId(userId)).thenReturn(12L);
        when(bookingSpotRepository.sumEstimatedPriceByUserId(userId)).thenReturn(100.0);
        when(vehicleRepository.countByUserId(userId)).thenReturn(2L);

        // Act
        Map<String, Object> stats = statsService.getStatsForUser(userId);

        // Assert
        assertEquals(5L, stats.get("availableSpots"));
        assertEquals(12L, stats.get("totalHours"));
        assertEquals(100.0, stats.get("amountSpent"));
        assertEquals(2L, stats.get("activeBookings"));
    }

    @Test
    void testGetStatsForUser_NoData() {
        // Arrange
        Long userId = 2L;
        when(bookingSpotRepository.countByUserId(userId)).thenReturn(0L);
        when(bookingSpotRepository.sumDurationByUserId(userId)).thenReturn(0L);
        when(bookingSpotRepository.sumEstimatedPriceByUserId(userId)).thenReturn(0.0);
        when(vehicleRepository.countByUserId(userId)).thenReturn(0L);

        // Act
        Map<String, Object> stats = statsService.getStatsForUser(userId);

        // Assert
        assertEquals(0L, stats.get("availableSpots"));
        assertEquals(0L, stats.get("totalHours"));
        assertEquals(0.0, stats.get("amountSpent"));
        assertEquals(0L, stats.get("activeBookings"));
    }

    @Test
    void testGetStatsForUser_Exception() {
        // Arrange
        Long userId = 1L;
        when(bookingSpotRepository.countByUserId(userId)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        try {
            statsService.getStatsForUser(userId);
        } catch (Exception e) {
            assertEquals("Database error", e.getMessage());
        }
    }

    @Test
    void testGetSpotOwnerStats() {
        // Arrange
        Long userId = 1L;
        when(parkingSpotRepository.countTotalSpotsByOwner(userId)).thenReturn(10);
        when(parkingSpotRepository.countEvSpotsByOwner(userId)).thenReturn(5);

        // Act
        Map<String, Object> stats = statsService.getSpotOwnerStats(userId);


    }

    @Test
    void testGetSpotOwnerStats_NoData() {
        // Arrange
        Long userId = 2L;
        when(parkingSpotRepository.countTotalSpotsByOwner(userId)).thenReturn(0);
        when(parkingSpotRepository.countEvSpotsByOwner(userId)).thenReturn(0);

        // Act
        Map<String, Object> stats = statsService.getSpotOwnerStats(userId);

    }

    @Test
    void testGetSpotOwnerStats_Exception() {
        // Arrange
        Long userId = 1L;
        when(parkingSpotRepository.countTotalSpotsByOwner(userId)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        try {
            statsService.getSpotOwnerStats(userId);
        } catch (Exception e) {
            assertEquals("Database error", e.getMessage());
        }
    }

    @Test
    void testGetRecentActivity() {
        // Arrange
        Long userId = 1L;
        int limit = 5;

        Map<String, Object> activity1 = new HashMap<>();
        activity1.put("bookingId", 1L);
        activity1.put("spotId", 10L);

        Map<String, Object> activity2 = new HashMap<>();
        activity2.put("bookingId", 2L);
        activity2.put("spotId", 11L);

        List<Map<String, Object>> recentActivity = Arrays.asList(activity1, activity2);
        Page<Map<String, Object>> page = new PageImpl<>(recentActivity);
        when(bookingSpotRepository.findRecentActivityByUserId(eq(userId), any(PageRequest.class))).thenReturn(page);

        // Act
        List<Map<String, Object>> result = statsService.getRecentActivity(userId, limit);

        // Assert
        assertEquals(2, result.size());
        assertEquals(activity1, result.get(0));
        assertEquals(activity2, result.get(1));
    }

    @Test
    void testGetRecentActivity_NoData() {
        // Arrange
        Long userId = 1L;
        int limit = 5;
        Page<Map<String, Object>> emptyPage = new PageImpl<>(Collections.emptyList());
        when(bookingSpotRepository.findRecentActivityByUserId(eq(userId), any(PageRequest.class))).thenReturn(emptyPage);

        // Act
        List<Map<String, Object>> result = statsService.getRecentActivity(userId, limit);

        // Assert
        assertEquals(0, result.size());
    }
}
