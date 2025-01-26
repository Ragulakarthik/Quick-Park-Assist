package com.quick_park_assist.controllerTest;

import com.quick_park_assist.controller.ViewBookingByMobileController;
import com.quick_park_assist.entity.BookingSpot;
import com.quick_park_assist.service.IViewBookingByMobileService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ViewBookingByMobileControllerTest {

    @Mock
    private IViewBookingByMobileService viewByMobileNumberService;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @InjectMocks
    private ViewBookingByMobileController viewBookingByMobileController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test case 1: User not in session
    @Test
    void testShowBookingForm_UserNotInSession() {
        // Arrange
        when(session.getAttribute("userId")).thenReturn(null);

        // Act
        String viewName = viewBookingByMobileController.showBookingForm(session, model);

        // Assert
        assertEquals("redirect:/login", viewName);
        verifyNoInteractions(model);
    }

    // Test case 2: User in session with confirmed bookings
    @Test
    void testShowBookingForm_UserInSessionWithBookings() {
        // Arrange
        Long userId = 1L;
        when(session.getAttribute("userId")).thenReturn(userId);

        List<BookingSpot> bookings = new ArrayList<>();
        bookings.add(new BookingSpot()); // Add dummy booking
        when(viewByMobileNumberService.getConfirmedBookingsByUserID(userId)).thenReturn(bookings);

        // Act
        String viewName = viewBookingByMobileController.showBookingForm(session, model);

        // Assert
        assertEquals("ViewBookingByMobile", viewName);
        verify(model).addAttribute("bookings", bookings);
        verify(model).addAttribute(eq("bookingSpot"), any(BookingSpot.class));
    }

    // Test case 3: User in session with no confirmed bookings
    @Test
    void testShowBookingForm_UserInSessionWithNoBookings() {
        // Arrange
        Long userId = 1L;
        when(session.getAttribute("userId")).thenReturn(userId);

        List<BookingSpot> bookings = new ArrayList<>(); // Empty list
        when(viewByMobileNumberService.getConfirmedBookingsByUserID(userId)).thenReturn(bookings);

        // Act
        String viewName = viewBookingByMobileController.showBookingForm(session, model);

        // Assert
        assertEquals("ViewBookingByMobile", viewName);
        verify(model).addAttribute("bookings", bookings);
        verify(model).addAttribute(eq("bookingSpot"), any(BookingSpot.class));
    }

    // Test case 4: Service returns null for bookings
    @Test
    void testShowBookingForm_ServiceReturnsNull() {
        // Arrange
        Long userId = 1L;
        when(session.getAttribute("userId")).thenReturn(userId);

        when(viewByMobileNumberService.getConfirmedBookingsByUserID(userId)).thenReturn(null);

        // Act
        String viewName = viewBookingByMobileController.showBookingForm(session, model);

        // Assert
        assertEquals("ViewBookingByMobile", viewName);
        verify(model).addAttribute("bookings", null);
        verify(model).addAttribute(eq("bookingSpot"), any(BookingSpot.class));
    }

    // Test case 5: Model attribute addition for bookingSpot
    @Test
    void testShowBookingForm_AddsBookingSpotAttribute() {
        // Arrange
        Long userId = 1L;
        when(session.getAttribute("userId")).thenReturn(userId);

        List<BookingSpot> bookings = new ArrayList<>();
        when(viewByMobileNumberService.getConfirmedBookingsByUserID(userId)).thenReturn(bookings);

        // Act
        String viewName = viewBookingByMobileController.showBookingForm(session, model);

        // Assert
        assertEquals("ViewBookingByMobile", viewName);
        verify(model).addAttribute(eq("bookingSpot"), any(BookingSpot.class));
    }
}
