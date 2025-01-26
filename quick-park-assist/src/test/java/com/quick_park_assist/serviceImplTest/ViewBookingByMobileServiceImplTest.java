package com.quick_park_assist.serviceImplTest;

import com.quick_park_assist.entity.BookingSpot;
import com.quick_park_assist.enums.BookingSpotStatus;
import com.quick_park_assist.repository.BookingSpotRepository;
import com.quick_park_assist.serviceImpl.ViewBookingByMobileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class ViewBookingByMobileServiceImplTest {

    @Mock
    private BookingSpotRepository bookingSpotRepository;

    @InjectMocks
    private ViewBookingByMobileServiceImpl viewBookingByMobileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test case 1: Valid user ID with confirmed bookings
    @Test
    void testGetConfirmedBookingsByUserID_ValidUserID() {
        // Arrange
        Long userId = 1L;
        List<BookingSpot> bookings = new ArrayList<>();
        bookings.add(new BookingSpot()); // Add a dummy booking
        when(bookingSpotRepository.findByUserIDAndBookingSpotStatus(userId, BookingSpotStatus.CONFIRMED)).thenReturn(bookings);

        // Act
        List<BookingSpot> result = viewBookingByMobileService.getConfirmedBookingsByUserID(userId);

        // Assert
        assertEquals(bookings, result);
        verify(bookingSpotRepository).findByUserIDAndBookingSpotStatus(userId, BookingSpotStatus.CONFIRMED);
    }

    // Test case 2: Valid user ID with no confirmed bookings
    @Test
    void testGetConfirmedBookingsByUserID_NoBookings() {
        // Arrange
        Long userId = 1L;
        List<BookingSpot> bookings = new ArrayList<>(); // Empty list
        when(bookingSpotRepository.findByUserIDAndBookingSpotStatus(userId, BookingSpotStatus.CONFIRMED)).thenReturn(bookings);

        // Act
        List<BookingSpot> result = viewBookingByMobileService.getConfirmedBookingsByUserID(userId);

        // Assert
        assertEquals(bookings, result);
        verify(bookingSpotRepository).findByUserIDAndBookingSpotStatus(userId, BookingSpotStatus.CONFIRMED);
    }


    // Test case 3: Repository returns null
    @Test
    void testGetConfirmedBookingsByUserID_RepositoryReturnsNull() {
        // Arrange
        Long userId = 1L;
        when(bookingSpotRepository.findByUserIDAndBookingSpotStatus(userId, BookingSpotStatus.CONFIRMED)).thenReturn(null);

        // Act
        List<BookingSpot> result = viewBookingByMobileService.getConfirmedBookingsByUserID(userId);

        // Assert
        assertNull(result);
        verify(bookingSpotRepository).findByUserIDAndBookingSpotStatus(userId, BookingSpotStatus.CONFIRMED);
    }
}
