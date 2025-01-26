package com.quick_park_assist.controllerTest;

import com.quick_park_assist.controller.CancelSpotController;
import com.quick_park_assist.entity.BookingSpot;
import com.quick_park_assist.service.ICancelSpotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CancelSpotControllerTest {

    @InjectMocks
    private CancelSpotController cancelSpotController;

    @Mock
    private ICancelSpotService cancelSpotService;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testShowCancelForm_UserNotLoggedIn() {
        when(session.getAttribute("userId")).thenReturn(null);
        String result = cancelSpotController.showCancelForm(session, model);
        assertEquals("redirect:/login", result);
    }

    @Test
    void testShowCancelForm_UserLoggedIn_NoBookings() {
        Long userId = 1L;
        when(session.getAttribute("userId")).thenReturn(userId);
        when(cancelSpotService.getConfirmedBookingsByUserID(userId)).thenReturn(new ArrayList<>());

        String result = cancelSpotController.showCancelForm(session, model);

        verify(model).addAttribute("bookings", new ArrayList<>());
        verify(model).addAttribute(eq("cancelSpot"), any(BookingSpot.class));
        assertEquals("CancelBooking", result);
    }

    @Test
    void testShowCancelForm_UserLoggedIn_WithBookings() {
        Long userId = 1L;
        List<BookingSpot> bookings = new ArrayList<>();
        BookingSpot booking = new BookingSpot();
        booking.setBookingId(1L);
        bookings.add(booking);

        when(session.getAttribute("userId")).thenReturn(userId);
        when(cancelSpotService.getConfirmedBookingsByUserID(userId)).thenReturn(bookings);

        String result = cancelSpotController.showCancelForm(session, model);

        verify(model).addAttribute("bookings", bookings);
        verify(model).addAttribute(eq("cancelSpot"), any(BookingSpot.class));
        assertEquals("CancelBooking", result);
    }

    @Test
    void testCancelSelectedBooking_InvalidBookingId() {
        String result = cancelSpotController.cancelSelectedBooking(null, redirectAttributes);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Invalid Booking ID.");
        assertEquals("redirect:/cancelSpot/", result);
    }

    @Test
    void testCancelSelectedBooking_CancellationSuccess() {
        Long bookingId = 1L;
        when(cancelSpotService.cancelBooking(bookingId)).thenReturn(true);

        String result = cancelSpotController.cancelSelectedBooking(bookingId, redirectAttributes);

        verify(redirectAttributes).addFlashAttribute("successMessage", "Booking successfully Cancelled!");
        assertEquals("redirect:/cancelSpot/", result);
    }

    @Test
    void testCancelSelectedBooking_CancellationFailure() {
        Long bookingId = 1L;
        when(cancelSpotService.cancelBooking(bookingId)).thenReturn(false);

        String result = cancelSpotController.cancelSelectedBooking(bookingId, redirectAttributes);

        verify(redirectAttributes).addFlashAttribute("errorMessage", "Cancellation failed.");
        assertEquals("redirect:/cancelSpot/", result);
    }
}
