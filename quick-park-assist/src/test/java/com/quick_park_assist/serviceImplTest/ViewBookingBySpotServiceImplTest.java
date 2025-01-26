package com.quick_park_assist.serviceImplTest;

import com.quick_park_assist.entity.BookingSpot;
import com.quick_park_assist.repository.BookingSpotRepository;
import com.quick_park_assist.serviceImpl.ViewBookingBySpotServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ViewBookingBySpotServiceImplTest {

    @Mock
    private BookingSpotRepository bookingSpotRepository;

    @InjectMocks
    private ViewBookingBySpotServiceImpl viewBookingBySpotService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test Case 1: Fetch bookings by spot location for a valid user with results
    @Test
    void testGetBookingsBySpotLocation_ValidUserWithResults() {
        // Arrange
        Long userId = 1L;
        String spotLocation = "Location1";
        List<BookingSpot> bookings = new ArrayList<>();
        BookingSpot bookingSpot = new BookingSpot();
        bookingSpot.setSpotLocation(spotLocation);
        bookings.add(bookingSpot);

        when(bookingSpotRepository.getBookingsBySpotLocationAndUserId(userId, spotLocation)).thenReturn(bookings);

        // Act
        List<BookingSpot> result = viewBookingBySpotService.getBookingsBySpotLocation(userId, spotLocation);

        // Assert
        assertEquals(1, result.size());
        assertEquals(spotLocation, result.get(0).getSpotLocation());
        verify(bookingSpotRepository, times(1)).getBookingsBySpotLocationAndUserId(userId, spotLocation);
    }

    // Test Case 2: Fetch bookings by spot location for a valid user with no results
    @Test
    void testGetBookingsBySpotLocation_ValidUserNoResults() {
        // Arrange
        Long userId = 1L;
        String spotLocation = "Location1";
        when(bookingSpotRepository.getBookingsBySpotLocationAndUserId(userId, spotLocation)).thenReturn(new ArrayList<>());

        // Act
        List<BookingSpot> result = viewBookingBySpotService.getBookingsBySpotLocation(userId, spotLocation);

        // Assert
        assertEquals(0, result.size());
        verify(bookingSpotRepository, times(1)).getBookingsBySpotLocationAndUserId(userId, spotLocation);
    }

    // Test Case 3: Fetch bookings with null userId
    @Test
    void testGetBookingsBySpotLocation_NullUserId() {
        // Arrange
        String spotLocation = "Location1";

        // Act
        List<BookingSpot> result = viewBookingBySpotService.getBookingsBySpotLocation(null, spotLocation);

        // Assert
        assertEquals(0, result.size());
        verify(bookingSpotRepository, times(1)).getBookingsBySpotLocationAndUserId(null, spotLocation);
    }

    // Test Case 4: Fetch bookings with null spot location
    @Test
    void testGetBookingsBySpotLocation_NullSpotLocation() {
        // Arrange
        Long userId = 1L;

        // Act
        List<BookingSpot> result = viewBookingBySpotService.getBookingsBySpotLocation(userId, null);

        // Assert
        assertEquals(0, result.size());
        verify(bookingSpotRepository, times(1)).getBookingsBySpotLocationAndUserId(userId, null);
    }

    // Test Case 5: Fetch bookings with both userId and spot location null
    @Test
    void testGetBookingsBySpotLocation_NullUserIdAndSpotLocation() {
        // Act
        List<BookingSpot> result = viewBookingBySpotService.getBookingsBySpotLocation(null, null);

        // Assert
        assertEquals(0, result.size());
        verify(bookingSpotRepository, times(1)).getBookingsBySpotLocationAndUserId(null, null);
    }
}
