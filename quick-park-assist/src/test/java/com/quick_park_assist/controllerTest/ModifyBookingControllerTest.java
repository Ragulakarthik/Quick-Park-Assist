package com.quick_park_assist.controllerTest;



import com.quick_park_assist.controller.ModifySpotController;
import com.quick_park_assist.entity.BookingSpot;
import com.quick_park_assist.service.IModifySpotService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ModifyBookingControllerTest {

    @Mock
    private IModifySpotService modifySpotService;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private ModifySpotController modifySpotController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test cases go here
    @Test
    void testFetchConfirmedBookings_UserNotInSession() {
        // Arrange
        when(session.getAttribute("userId")).thenReturn(null);

        // Act
        String viewName = modifySpotController.fetchConfirmedBookings(session, model);

        // Assert
        assertEquals("redirect:/login", viewName);
        verifyNoInteractions(model);
    }
    //second Test-case
    @Test
    void testFetchConfirmedBookings_UserInSession() {
        // Arrange
        Long userId = 1L;
        when(session.getAttribute("userId")).thenReturn(userId);

        List<BookingSpot> bookings = new ArrayList<>();
        bookings.add(new BookingSpot()); // Add dummy booking
        when(modifySpotService.getConfirmedBookings(userId)).thenReturn(bookings);

        // Act
        String viewName = modifySpotController.fetchConfirmedBookings(session, model);

        // Assert
        assertEquals("ModifyBooking", viewName);
        verify(model).addAttribute("bookings", bookings);
    }
    //third -Testcase
    @Test
    void testHandleGetRequest() {
        // Act
        String viewName = modifySpotController.handleGetRequest();

        // Assert
        assertEquals("redirect:/modifySpot/", viewName);
    }
    //forth -testCase
    @Test
    void testUpdateSpotDetails_ValidData() throws Exception {
        // Arrange
        Long bookingId = 1L;
        Long spotID = 1L;
        String startTimeStr = "2026-01-25T10:00";
        Double duration = 2.0;

        Date startTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(startTimeStr);
        when(modifySpotService.updateSpotDetails(bookingId, startTime, duration, spotID)).thenReturn(true);

        // Act
        String viewName = modifySpotController.updateSpotDetails(bookingId, spotID, startTimeStr, duration, redirectAttributes);

        // Assert
        assertEquals("redirect:/modifySpot/", viewName);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Booking updated successfully!");
    }
    //fifth-TestCase
    @Test
    void testUpdateSpotDetails_InvalidStartTime() throws Exception {
        // Arrange
        Long bookingId = 1L;
        Long spotID = 101L;
        String startTimeStr = "2023-01-01T10:00"; // Past date
        Double duration = 2.0;

        // Act
        String viewName = modifySpotController.updateSpotDetails(bookingId, spotID, startTimeStr, duration, redirectAttributes);

        // Assert
        assertEquals("redirect:/modifySpot/", viewName);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Please select a future date and time.");
        verifyNoInteractions(modifySpotService);
    }
    //sixth-TestCase
    @Test
    void testUpdateSpotDetails_InvalidDuration() {
        // Arrange
        Long bookingId = 1L;
        Long spotID = 101L;
        String startTimeStr = "2025-01-25T10:00";
        Double duration = -1.0; // Invalid duration

        // Act
        String viewName = modifySpotController.updateSpotDetails(bookingId, spotID, startTimeStr, duration, redirectAttributes);

        // Assert
        assertEquals("redirect:/modifySpot/", viewName);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Please select a future date and time.");
        verifyNoInteractions(modifySpotService);
    }
    //seventh-TestCase
    @Test
    void testUpdateSpotDetails_InvalidDateFormat() {
        // Arrange
        Long bookingId = 1L;
        Long spotID = 101L;
        String startTimeStr = "invalid-date";
        Double duration = 2.0;

        // Act
        String viewName = modifySpotController.updateSpotDetails(bookingId, spotID, startTimeStr, duration, redirectAttributes);

        // Assert
        assertEquals("redirect:/modifySpot/", viewName);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Invalid date format. Please use the correct format.Try Again");
        verifyNoInteractions(modifySpotService);
    }


}

