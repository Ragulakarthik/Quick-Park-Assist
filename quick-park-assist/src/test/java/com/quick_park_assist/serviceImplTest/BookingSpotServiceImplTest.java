package com.quick_park_assist.serviceImplTest;


import com.quick_park_assist.entity.BookingSpot;
import com.quick_park_assist.repository.BookingSpotRepository;
import com.quick_park_assist.serviceImpl.BookingSpotServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingSpotServiceImplTest {

    @InjectMocks
    private BookingSpotServiceImpl bookingSpotService;

    @Mock
    private BookingSpotRepository bookingSpotRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveBookingSpot() {
        // Arrange
        BookingSpot bookingSpot = new BookingSpot();

        // Act
        bookingSpotService.saveBookingSpot(bookingSpot);

        // Assert
        verify(bookingSpotRepository, times(1)).save(bookingSpot);
    }


    @Test
    void testCheckIfPreviouslyBooked_NoPreviousBooking() {
        Long userId = 1L;
        Long spotId = 2L;
        Date startTime = new Date();

        when(bookingSpotRepository.findTopLastBookingSpotByUserIdAndSpotId(userId, spotId)).thenReturn(Optional.empty());

        boolean result = bookingSpotService.checkIfPreviouslyBooked(userId, spotId, startTime);

        assertTrue(result);
        verify(bookingSpotRepository, times(1)).findTopLastBookingSpotByUserIdAndSpotId(userId, spotId);
    }

    @Test
    void testCheckIfPreviouslyBooked_PreviousBookingValid() {
        Long userId = 1L;
        Long spotId = 2L;
        Date startTime = new Date();
        BookingSpot bookingSpot = new BookingSpot();
        bookingSpot.setEndTime(new Date(startTime.getTime() - 1000)); // End time before the new start time

        when(bookingSpotRepository.findTopLastBookingSpotByUserIdAndSpotId(userId, spotId)).thenReturn(Optional.of(bookingSpot));

        boolean result = bookingSpotService.checkIfPreviouslyBooked(userId, spotId, startTime);

        assertTrue(result);
        verify(bookingSpotRepository, times(1)).findTopLastBookingSpotByUserIdAndSpotId(userId, spotId);
    }

    @Test
    void testCheckIfPreviouslyBooked_PreviousBookingInvalid() {
        Long userId = 1L;
        Long spotId = 2L;
        Date startTime = new Date();
        BookingSpot bookingSpot = new BookingSpot();
        bookingSpot.setEndTime(new Date(startTime.getTime() + 1000)); // End time after the new start time

        when(bookingSpotRepository.findTopLastBookingSpotByUserIdAndSpotId(userId, spotId)).thenReturn(Optional.of(bookingSpot));

        boolean result = bookingSpotService.checkIfPreviouslyBooked(userId, spotId, startTime);

        assertFalse(result);
        verify(bookingSpotRepository, times(1)).findTopLastBookingSpotByUserIdAndSpotId(userId, spotId);
    }
}

