package com.quick_park_assist.controllerTest;

import com.quick_park_assist.controller.ViewBookingBySpotController;
import com.quick_park_assist.entity.BookingSpot;
import com.quick_park_assist.service.IViewBookingBySpotService;
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

class ViewBookingBySpotControllerTest {

    @Mock
    private IViewBookingBySpotService viewBySpotService;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @InjectMocks
    private ViewBookingBySpotController viewBookingBySpotController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test Case 1: User not logged in when accessing the form
    @Test
    void testShowSpotForm_UserNotLoggedIn() {
        // Arrange
        when(session.getAttribute("userId")).thenReturn(null);

        // Act
        String viewName = viewBookingBySpotController.showSpotForm(session, model);

        // Assert
        assertEquals("redirect:/login", viewName);
        verifyNoInteractions(model);
    }

    // Test Case 2: User logged in when accessing the form
    @Test
    void testShowSpotForm_UserLoggedIn() {
        // Arrange
        Long userId = 1L;
        when(session.getAttribute("userId")).thenReturn(userId);

        // Act
        String viewName = viewBookingBySpotController.showSpotForm(session, model);

        // Assert
        assertEquals("ViewBookingBySpot", viewName);
        verify(model).addAttribute(eq("bookingSpot"), any(BookingSpot.class));
    }

    // Test Case 3: User not logged in when fetching bookings
    @Test
    void testGetBookingDetails_UserNotLoggedIn() {
        // Arrange
        when(session.getAttribute("userId")).thenReturn(null);

        // Act
        String viewName = viewBookingBySpotController.getBookingDetails("Location1", session, model);

        // Assert
        assertEquals("redirect:/login", viewName);
        verifyNoInteractions(model);
    }
    @Test
    void testShowSpotForm_UserNotLoggedIn_RedirectsToLogin() {
        // Act
        String view = viewBookingBySpotController.showSpotForm(session, model);

        // Assert
        assertEquals("redirect:/login", view, "Expected redirect to login when user is not logged in.");
        verify(model, never()).addAttribute(anyString(), any());
    }

    @Test
    void testShowSpotForm_UserLoggedIn_ReturnsViewBookingBySpot() {
        // Arrange
        Long userId = 1L;
        when(session.getAttribute("userId")).thenReturn(userId);

        // Act
        String view = viewBookingBySpotController.showSpotForm(session, model);

        // Assert
        assertEquals("ViewBookingBySpot", view, "Expected ViewBookingBySpot view when user is logged in.");
        verify(model, times(1)).addAttribute(eq("bookingSpot"), any(BookingSpot.class));
    }

    @Test
    void testGetBookingDetails_UserNotLoggedIn_RedirectsToLogin() {
        // Arrange
        String spotLocation = "Zone A";

        // Act
        String view = viewBookingBySpotController.getBookingDetails(spotLocation, session, model);

        // Assert
        assertEquals("redirect:/login", view, "Expected redirect to login when user is not logged in.");
        verify(model, never()).addAttribute(anyString(), any());
    }

    @Test
    void testGetBookingDetails_UserLoggedIn_NoBookings_ReturnsViewBookingBySpot() {
        // Arrange
        Long userId = 1L;
        when(session.getAttribute("userId")).thenReturn(userId);
        String spotLocation = "Zone A";
        List<BookingSpot> bookings = new ArrayList<>();
        when(viewBySpotService.getBookingsBySpotLocation(1L, spotLocation)).thenReturn(bookings);

        // Act
        String view = viewBookingBySpotController.getBookingDetails(spotLocation, session, model);

        // Assert
        assertEquals("ViewBookingBySpot", view, "Expected ViewBookingBySpot view when no bookings are found.");
        verify(model, times(1)).addAttribute("spotLocation", spotLocation);
        verify(model, times(2)).addAttribute("bookings", bookings);
    }

    @Test
    void testGetBookingDetails_UserLoggedIn_WithBookings_ReturnsViewBookingBySpot() {
        // Arrange
        Long userId = 1L;
        when(session.getAttribute("userId")).thenReturn(userId);
        String spotLocation = "Zone B";
        List<BookingSpot> bookings = new ArrayList<>();
        bookings.add(new BookingSpot());
        bookings.add(new BookingSpot());
        when(viewBySpotService.getBookingsBySpotLocation(1L, spotLocation)).thenReturn(bookings);

        // Act
        String view = viewBookingBySpotController.getBookingDetails(spotLocation, session, model);

        // Assert
        assertEquals("ViewBookingBySpot", view, "Expected ViewBookingBySpot view when bookings are found.");
        verify(model, times(1)).addAttribute("spotLocation", spotLocation);
        verify(model, times(2)).addAttribute("bookings", bookings);
    }
}


