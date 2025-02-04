package com.quick_park_assist.serviceImplTest;


import com.quick_park_assist.entity.BookingSpot;
import com.quick_park_assist.entity.ParkingSpot;
import com.quick_park_assist.entity.User;
import com.quick_park_assist.repository.BookingSpotRepository;
import com.quick_park_assist.serviceImpl.BookingHistoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingHistoryServiceImplTest {

    @Mock
    private BookingSpotRepository bookingSpotRepository;

    @InjectMocks
    private BookingHistoryServiceImpl bookingHistoryService;

    private User user;
    private ParkingSpot parkingSpot;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);

        parkingSpot = new ParkingSpot();
        parkingSpot.setLocation("Spot A");
        parkingSpot.setSpotLocation("Spot B");
    }

    @Test
    void testGetBookingsByUserID_Success() {

        // Mock data
        BookingSpot booking1 = new BookingSpot();
        booking1.setBookingId(101L);
        booking1.setUser(user);
        booking1.setSpot(parkingSpot);

        BookingSpot booking2 = new BookingSpot();
        booking2.setBookingId(102L);
        booking2.setUser(user);
        booking2.setSpot(parkingSpot);

        List<BookingSpot> mockBookings = Arrays.asList(booking1, booking2);

        // Mock repository response
        when(bookingSpotRepository.findBookingsByUserId(user.getId())).thenReturn(mockBookings);

        // Test method
        List<BookingSpot> result = bookingHistoryService.getBookingsByuserID(user.getId());

        // Assertions
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(101L, result.get(0).getBookingId());
        assertEquals(102L, result.get(1).getBookingId());

        // Verify interaction
        verify(bookingSpotRepository, times(1)).findBookingsByUserId(user.getId());
    }

    @Test
    void testGetBookingsByUserID_EmptyList() {
        Long userId = 2L;

        // Mock repository response
        when(bookingSpotRepository.findBookingsByUserId(userId)).thenReturn(Arrays.asList());

        // Test method
        List<BookingSpot> result = bookingHistoryService.getBookingsByuserID(userId);

        // Assertions
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify interaction
        verify(bookingSpotRepository, times(1)).findBookingsByUserId(userId);
    }

    @Test
    void testGetBookingsByUserID_NullUserID() {
        Long userId = null;

        // Mock repository response
        when(bookingSpotRepository.findBookingsByUserId(userId)).thenReturn(null);

        // Test method
        List<BookingSpot> result = bookingHistoryService.getBookingsByuserID(userId);

        // Assertions
        assertNull(result);

        // Verify interaction
        verify(bookingSpotRepository, times(1)).findBookingsByUserId(userId);
    }

    @Test
    void testGetBookingsByUserID_RepositoryException() {
        Long userId = 1L;

        // Mock exception
        when(bookingSpotRepository.findBookingsByUserId(userId))
                .thenThrow(new RuntimeException("Database error"));

        // Test method
        Exception exception = assertThrows(RuntimeException.class, () -> {
            bookingHistoryService.getBookingsByuserID(userId);
        });

        // Assertions
        assertEquals("Database error", exception.getMessage());

        // Verify interaction
        verify(bookingSpotRepository, times(1)).findBookingsByUserId(userId);
    }
}

