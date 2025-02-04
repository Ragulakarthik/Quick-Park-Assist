package com.quick_park_assist.controllerTest;

import com.quick_park_assist.controller.BookingHistoryController;
import com.quick_park_assist.entity.BookingSpot;
import com.quick_park_assist.service.IBookingHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class BookingHistoryControllerTest {
    @Mock
    private IBookingHistoryService bookingHistoryService;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @InjectMocks
    private BookingHistoryController bookingHistoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test cases go here
    @Test
    void testShowHistoryForm_UserNotInSession() {
        // Arrange
        when(session.getAttribute("userId")).thenReturn(null);

        // Act
        String viewName = bookingHistoryController.showHistoryForm(session, model);

        // Assert
        assertEquals("redirect:/login", viewName);
        verifyNoInteractions(model);
    }
    // second TestCase
    @Test
    void testShowHistoryForm_UserInSession() {
        // Arrange
        Long userId = 1L;
        when(session.getAttribute("userId")).thenReturn(userId);

        List<BookingSpot> bookings = new ArrayList<>();
        bookings.add(new BookingSpot()); // Add a dummy booking

        when(bookingHistoryService.getBookingsByuserID(userId)).thenReturn(bookings);

        // Act
        String viewName = bookingHistoryController.showHistoryForm(session, model);

        // Assert
        assertEquals("BookingHistory", viewName);
        verify(model).addAttribute("bookings", bookings);
        verify(model).addAttribute(eq("cancelSpot"), any(BookingSpot.class));
    }
    //third TestCase
    @Test
    void testHandleGetRequest() {
        // Act
        String viewName = bookingHistoryController.handleGetRequest();

        // Assert
        assertEquals("redirect:/bookingHistory/", viewName);
    }


}
