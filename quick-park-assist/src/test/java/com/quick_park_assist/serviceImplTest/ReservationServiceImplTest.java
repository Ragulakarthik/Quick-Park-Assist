package com.quick_park_assist.serviceImplTest;

import com.quick_park_assist.entity.Reservation;
import com.quick_park_assist.repository.ReservationRepository;
import com.quick_park_assist.serviceImpl.ReservationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReservationServiceImplTest {

    @InjectMocks
    private ReservationServiceImpl reservationService;

    @Mock
    private ReservationRepository reservationRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetReservationsByUserId() {
        Long userId = 1L;
        List<Reservation> mockReservations = List.of(new Reservation(), new Reservation());

        when(reservationRepository.findByUserId(userId)).thenReturn(mockReservations);

        List<Reservation> reservations = reservationService.getReservationsByUserId(userId);

        assertNotNull(reservations);
        assertEquals(2, reservations.size());
        verify(reservationRepository, times(1)).findByUserId(userId);
    }

    @Test
    void testUpdateSpotDetails_BookingExists() {
        Long bookingId = 1L;
        Date startTime = new Date();
        String vehicleNumber = "EV123";

        Reservation mockReservation = new Reservation();
        mockReservation.setId(bookingId);
        mockReservation.setReservationTime(new Date());
        mockReservation.setVehicleNumber("OLD123");

        when(reservationRepository.findById(bookingId)).thenReturn(Optional.of(mockReservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(mockReservation);

        boolean result = reservationService.updateSpotDetails(bookingId, startTime, vehicleNumber);

        assertTrue(result);
        assertEquals(startTime, mockReservation.getReservationTime());
        assertEquals(vehicleNumber, mockReservation.getVehicleNumber());
        verify(reservationRepository, times(1)).findById(bookingId);
        verify(reservationRepository, times(1)).save(mockReservation);
    }

    @Test
    void testUpdateSpotDetails_BookingDoesNotExist() {
        Long bookingId = 1L;
        Date startTime = new Date();
        String vehicleNumber = "EV123";

        when(reservationRepository.findById(bookingId)).thenReturn(Optional.empty());

        boolean result = reservationService.updateSpotDetails(bookingId, startTime, vehicleNumber);

        assertFalse(result);
        verify(reservationRepository, times(1)).findById(bookingId);
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void testGetReservationById_Found() {
        Long reservationId = 1L;
        Reservation mockReservation = new Reservation();
        mockReservation.setId(reservationId);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(mockReservation));

        Reservation reservation = reservationService.getReservationById(reservationId);

        assertNotNull(reservation);
        assertEquals(reservationId, reservation.getId());
        verify(reservationRepository, times(1)).findById(reservationId);
    }

    @Test
    void testGetReservationById_NotFound() {
        Long reservationId = 1L;

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

        Reservation reservation = reservationService.getReservationById(reservationId);

        assertNull(reservation);
        verify(reservationRepository, times(1)).findById(reservationId);
    }

    @Test
    void testAddReservation() {
        Reservation mockReservation = new Reservation();

        when(reservationRepository.save(mockReservation)).thenReturn(mockReservation);

        reservationService.addReservation(mockReservation);

        verify(reservationRepository, times(1)).save(mockReservation);
    }

    @Test
    void testDeleteReservationById_Found() {
        Long reservationId = 1L;
        Reservation mockReservation = new Reservation();
        mockReservation.setId(reservationId);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(mockReservation));

        boolean result = reservationService.deleteReservationById(reservationId);

        assertTrue(result);
        verify(reservationRepository, times(1)).findById(reservationId);
        verify(reservationRepository, times(1)).delete(mockReservation);
    }

    @Test
    void testDeleteReservationById_NotFound() {
        Long reservationId = 1L;

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

        boolean result = reservationService.deleteReservationById(reservationId);

        assertFalse(result);
        verify(reservationRepository, times(1)).findById(reservationId);
        verify(reservationRepository, never()).delete(any(Reservation.class));
    }
}
