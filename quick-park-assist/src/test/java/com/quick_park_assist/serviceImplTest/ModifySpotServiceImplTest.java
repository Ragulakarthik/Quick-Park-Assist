package com.quick_park_assist.serviceImplTest;


import com.quick_park_assist.entity.BookingSpot;
import com.quick_park_assist.entity.ParkingSpot;
import com.quick_park_assist.enums.BookingSpotStatus;
import com.quick_park_assist.repository.BookingSpotRepository;
import com.quick_park_assist.repository.ParkingSpotRepository;
import com.quick_park_assist.serviceImpl.ModifySpotServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ModifySpotServiceImplTest {

    @Mock
    private BookingSpotRepository bookingSpotRepository;

    @Mock
    private ParkingSpotRepository parkingSpotRepository;

    @InjectMocks
    private ModifySpotServiceImpl modifySpotService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetConfirmedBookings_WithValidUserId() {
        // Arrange
        Long userId = 1L;
        List<BookingSpot> mockBookings = new ArrayList<>();
        mockBookings.add(new BookingSpot());
        mockBookings.add(new BookingSpot());

        when(bookingSpotRepository.findByUserIDAndBookingSpotStatus(userId, BookingSpotStatus.CONFIRMED))
                .thenReturn(mockBookings);

        // Act
        List<BookingSpot> result = modifySpotService.getConfirmedBookings(userId);

        // Assert
        assertEquals(2, result.size());
        verify(bookingSpotRepository, times(1))
                .findByUserIDAndBookingSpotStatus(userId, BookingSpotStatus.CONFIRMED);
    }

    @Test
    void testGetConfirmedBookings_WithNoBookings() {
        // Arrange
        Long userId = 2L;
        when(bookingSpotRepository.findByUserIDAndBookingSpotStatus(userId, BookingSpotStatus.CONFIRMED))
                .thenReturn(new ArrayList<>());

        // Act
        List<BookingSpot> result = modifySpotService.getConfirmedBookings(userId);

        // Assert
        assertTrue(result.isEmpty());
        verify(bookingSpotRepository, times(1))
                .findByUserIDAndBookingSpotStatus(userId, BookingSpotStatus.CONFIRMED);
    }

    @Test
    void testUpdateSpotDetails_WithValidInputs() {
        // Arrange
        Long bookingId = 1L;
        Long spotId = 1L;
        Date startTime = new Date();
        Double duration = 2.5;
        Double spotPricePerHour = 50.0;

        BookingSpot mockBookingSpot = new BookingSpot();
        mockBookingSpot.setBookingId(bookingId);

        ParkingSpot mockParkingSpot = new ParkingSpot();
        mockParkingSpot.setPricePerHour(spotPricePerHour);

        when(bookingSpotRepository.findById(bookingId)).thenReturn(Optional.of(mockBookingSpot));
        when(parkingSpotRepository.findById(spotId)).thenReturn(Optional.of(mockParkingSpot));

        // Act
        boolean result = modifySpotService.updateSpotDetails(bookingId, startTime, duration, spotId);

        // Assert
        assertTrue(result);
        verify(bookingSpotRepository, times(1)).save(mockBookingSpot);
        assertEquals(startTime, mockBookingSpot.getStartTime());
        assertEquals(duration, mockBookingSpot.getDuration());
        assertEquals(spotPricePerHour * duration, mockBookingSpot.getEstimatedPrice());
        assertNotNull(mockBookingSpot.getEndTime());
    }

    @Test
    void testUpdateSpotDetails_WithNonexistentBookingId() {
        // Arrange
        Long bookingId = 2L;
        Date startTime = new Date();
        Double duration = 1.5;
        Long spotId = 1L;

        when(bookingSpotRepository.findById(bookingId)).thenReturn(Optional.empty());

        // Act
        boolean result = modifySpotService.updateSpotDetails(bookingId, startTime, duration, spotId);

        // Assert
        assertFalse(result);
        verify(bookingSpotRepository, times(0)).save(any());
    }

    @Test
    void testUpdateSpotDetails_WithNonexistentParkingSpotId() {
        // Arrange
        Long bookingId = 1L;
        Long spotId = 2L;
        Date startTime = new Date();
        Double duration = 1.5;

        BookingSpot mockBookingSpot = new BookingSpot();
        mockBookingSpot.setBookingId(bookingId);

        when(bookingSpotRepository.findById(bookingId)).thenReturn(Optional.of(mockBookingSpot));
        when(parkingSpotRepository.findById(spotId)).thenReturn(Optional.empty());

        // Act
        boolean result = modifySpotService.updateSpotDetails(bookingId, startTime, duration, spotId);

        // Assert
        assertFalse(result);
        verify(bookingSpotRepository, times(0)).save(any());
    }

    @Test
    void testUpdateSpotDetails_WithZeroDuration() {
        // Arrange
        Long bookingId = 1L;
        Long spotId = 1L;
        Date startTime = new Date();
        Double duration = 0.0;

        BookingSpot mockBookingSpot = new BookingSpot();
        mockBookingSpot.setBookingId(bookingId);

        ParkingSpot mockParkingSpot = new ParkingSpot();
        mockParkingSpot.setPricePerHour(50.0);

        when(bookingSpotRepository.findById(bookingId)).thenReturn(Optional.of(mockBookingSpot));
        when(parkingSpotRepository.findById(spotId)).thenReturn(Optional.of(mockParkingSpot));

        // Act
        boolean result = modifySpotService.updateSpotDetails(bookingId, startTime, duration, spotId);

        // Assert
        assertTrue(result);
        verify(bookingSpotRepository, times(1)).save(mockBookingSpot);
        assertEquals(startTime, mockBookingSpot.getStartTime());
        assertEquals(duration, mockBookingSpot.getDuration());
        assertEquals(0.0, mockBookingSpot.getEstimatedPrice());
        assertNotNull(mockBookingSpot.getEndTime());
    }
}
