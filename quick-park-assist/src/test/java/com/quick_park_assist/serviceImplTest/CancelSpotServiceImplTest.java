package com.quick_park_assist.serviceImplTest;



import com.quick_park_assist.entity.BookingSpot;
import com.quick_park_assist.entity.User;
import com.quick_park_assist.enums.BookingSpotStatus;
import com.quick_park_assist.repository.BookingSpotRepository;
import com.quick_park_assist.serviceImpl.CancelSpotServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class CancelSpotServiceImplTest {

    @Mock
    private BookingSpotRepository bookingSpotRepository;

    @InjectMocks
    private CancelSpotServiceImpl cancelSpotService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCancelBooking_Success() {
        Long bookingId = 1L;
        BookingSpot bookingSpot = new BookingSpot();
        bookingSpot.setBookingId(bookingId);
        bookingSpot.setBookingSpotStatus(BookingSpotStatus.CONFIRMED);

        // Mock the repository to find the booking
        given(bookingSpotRepository.findByBookingId(bookingId)).willReturn(Optional.of(bookingSpot));

        // Perform the cancelBooking operation
        boolean result = cancelSpotService.cancelBooking(bookingId);

        // Assertions
        assertTrue(result);
        assertEquals(BookingSpotStatus.CANCELLED, bookingSpot.getBookingSpotStatus());
        verify(bookingSpotRepository, times(1)).save(bookingSpot);
    }

    @Test
    void testCancelBooking_NotFound() {
        Long bookingId = 1L;

        // Mock the repository to return an empty optional
        given(bookingSpotRepository.findByBookingId(bookingId)).willReturn(Optional.empty());

        // Perform the cancelBooking operation
        boolean result = cancelSpotService.cancelBooking(bookingId);

        // Assertions
        assertFalse(result);
        verify(bookingSpotRepository, never()).save(any(BookingSpot.class));
    }

    @Test
    void testGetConfirmedBookingsByUserID() {
        User user = new User();
        Long userId = 1L;
        user.setId(userId);
        BookingSpot bookingSpot = new BookingSpot();
        bookingSpot.setBookingId(1L);
        bookingSpot.setUser(user);
        bookingSpot.setBookingSpotStatus(BookingSpotStatus.CONFIRMED);

        // Mock the repository to return a list of confirmed bookings
        given(bookingSpotRepository.findByUserIDAndBookingSpotStatus(userId, BookingSpotStatus.CONFIRMED))
                .willReturn(Collections.singletonList(bookingSpot));

        // Perform the getConfirmedBookingsByUserID operation
        List<BookingSpot> result = cancelSpotService.getConfirmedBookingsByUserID(userId);

        // Assertions
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(BookingSpotStatus.CONFIRMED, result.get(0).getBookingSpotStatus());
        verify(bookingSpotRepository, times(1))
                .findByUserIDAndBookingSpotStatus(userId, BookingSpotStatus.CONFIRMED);
    }

    @Test
    void testGetConfirmedBookingsByUserID_NoResults() {
        Long userId = 1L;

        // Mock the repository to return an empty list
        given(bookingSpotRepository.findByUserIDAndBookingSpotStatus(userId, BookingSpotStatus.CONFIRMED))
                .willReturn(Collections.emptyList());

        // Perform the getConfirmedBookingsByUserID operation
        List<BookingSpot> result = cancelSpotService.getConfirmedBookingsByUserID(userId);

        // Assertions
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(bookingSpotRepository, times(1))
                .findByUserIDAndBookingSpotStatus(userId, BookingSpotStatus.CONFIRMED);
    }
}
