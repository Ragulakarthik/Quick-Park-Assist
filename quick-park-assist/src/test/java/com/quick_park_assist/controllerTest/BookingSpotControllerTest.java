package com.quick_park_assist.controllerTest;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.System.Logger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.quick_park_assist.controller.BookingSpotController;
import com.quick_park_assist.entity.BookingSpot;
import com.quick_park_assist.entity.ParkingSpot;
import com.quick_park_assist.entity.User;
import com.quick_park_assist.repository.BookingSpotRepository;
import com.quick_park_assist.repository.ParkingSpotRepository;
import com.quick_park_assist.repository.UserRepository;
import com.quick_park_assist.service.IBookingSpotService;
import com.quick_park_assist.service.IParkingSpotService;

import jakarta.servlet.http.HttpSession;

class BookingSpotControllerTest {

    @InjectMocks
    private BookingSpotController bookingSpotController;

    @Mock
    private IBookingSpotService bookingSpotService;

    @Mock
    private IParkingSpotService parkingSpotService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ParkingSpotRepository parkingSpotRepository;
    @Mock
    private BookingSpotRepository bookingSpotRepository;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private Logger mockLogger;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this
        );
    }

    @Test
    void testShowBookingSpotForm_UserNotLoggedIn() {
        when(session.getAttribute("userId")).thenReturn(null);
        String result = bookingSpotController.showBookingSpotForm(null, session, model);
        assertEquals("redirect:/login", result);
    }

    @Test
    void testShowBookingSpotForm_UserLoggedIn() {
        Long userId = 1L;
        User user = new User();
        user.setEmail("test@example.com");

        // Mock session attributes
        when(session.getAttribute("userId")).thenReturn(userId);
        when(session.getAttribute("loggedInUser")).thenReturn(user);

        // Mock repository behavior
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Call the method
        String result = bookingSpotController.showBookingSpotForm(null, session, model);

        // Verify interactions
        verify(model).addAttribute("loggedInUser", user);
        verify(model).addAttribute(eq("bookingSpot"), any(BookingSpot.class));

        // Assert result
        assertEquals("myBookingSpot", result);
    }
    
    @Test
    void testShowBookingSpotForm_notloggedin() {
        Long userId = 1L;
        User user = new User();
        user.setEmail("test@example.com");

        // Mock session attributes
        when(session.getAttribute("userId")).thenReturn(userId);
        when(session.getAttribute("loggedInUser")).thenReturn(user);

        // Mock repository behavior
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // Call the method
        String result = bookingSpotController.showBookingSpotForm(null, session, model);

        // Verify interactions
        verify(model).addAttribute("loggedInUser", user);
        verify(model).addAttribute(eq("bookingSpot"), any(BookingSpot.class));

        // Assert result
        assertEquals("myBookingSpot", result);
    }

    @Test
    void testSearchParkingSpots() {
        String query = "Central";
        User user = new User();
        user.setEmail("test@example.com");
        List<ParkingSpot> spots = new ArrayList<>();
        ParkingSpot spot = new ParkingSpot();
        spot.setId(1L);
        spot.setLocation("Central");
        spots.add(spot);

        when(session.getAttribute("loggedInUser")).thenReturn(user);
        when(parkingSpotService.getAllAvailableSpots(query)).thenReturn(spots);
        try {
            List<Map<String, String>> result = bookingSpotController.searchParkingSpots(query, null, session, model);

            assertEquals(1, result.size());
            assertEquals("1", result.get(0).get("id"));
            assertEquals("Central", result.get(0).get("location"));
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    void testSearchParkingSpotsCurrentUserIsPresent() {
        String query = "Central";
        User user = new User();
        user.setEmail("test@example.com");
        List<ParkingSpot> spots = new ArrayList<>();
        ParkingSpot spot = new ParkingSpot();
        spot.setId(1L);
        spot.setLocation("Central");
        spots.add(spot);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(session.getAttribute("loggedInUser")).thenReturn(user);
        when(parkingSpotService.getAllAvailableSpots(query)).thenReturn(spots);
        try {
            List<Map<String, String>> result = bookingSpotController.searchParkingSpots(query, null, session, model);

            assertEquals(1, result.size());
            assertEquals("1", result.get(0).get("id"));
            assertEquals("Central", result.get(0).get("location"));
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    @Test
    void redirectToBookingPage() {
    	String result=bookingSpotController.redirectToBookingPage();
    	assertEquals("redirect:/bookingSpot/",result);
    }
    @Test
    void testSubmitBookingSpotForm_InvalidStartTime() {
        BookingSpot bookingSpot = new BookingSpot();
        bookingSpot.setStartTime(new Date(System.currentTimeMillis() - 3600 * 1000)); // Past time

        when(session.getAttribute("userId")).thenReturn(1L);

        String result = bookingSpotController.submitBookingSpotForm(1L, "Central", redirectAttributes, bookingSpot, session);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Start time must be in the future.");
        assertEquals("redirect:/bookingSpot/", result);
    }

    @Test
    void testSubmitBookingSpotForm_SuccessfulBooking() {
        Long spotId = 1L;
        String spotLocation = "Central";
        BookingSpot bookingSpot = new BookingSpot();
        bookingSpot.setStartTime(new Date(System.currentTimeMillis() + 3600 * 1000)); // Future time
        bookingSpot.setDuration(2.0);

        User user = new User();
        user.setId(1L);
        user.setPhoneNumber("1234567890");

        ParkingSpot parkingSpot = new ParkingSpot();
        parkingSpot.setId(spotId);
        parkingSpot.setLocation(spotLocation);

        when(session.getAttribute("userId")).thenReturn(user.getId());
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(parkingSpotRepository.findById(spotId)).thenReturn(Optional.of(parkingSpot));
        when(bookingSpotService.checkIfPreviouslyBooked(user.getId(), spotId, bookingSpot.getStartTime())).thenReturn(true);

        String result = bookingSpotController.submitBookingSpotForm(spotId, spotLocation, redirectAttributes, bookingSpot, session);

        verify(bookingSpotRepository).save(any(BookingSpot.class));
        verify(redirectAttributes).addFlashAttribute("successMessage", "Your booking is successful!");
        assertEquals("redirect:/bookingSpot/", result);
    }
    
    @Test
    void testSubmitBookingSpotForm_durationnull() {
        Long spotId = 1L;
        String spotLocation = "Central";
        BookingSpot bookingSpot = new BookingSpot();
        bookingSpot.setStartTime(new Date(System.currentTimeMillis() + 3600 * 1000)); // Future time

        User user = new User();
        user.setId(1L);
        user.setPhoneNumber("1234567890");

        ParkingSpot parkingSpot = new ParkingSpot();
        parkingSpot.setId(spotId);
        parkingSpot.setLocation(spotLocation);

        when(session.getAttribute("userId")).thenReturn(user.getId());
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(parkingSpotRepository.findById(spotId)).thenReturn(Optional.of(parkingSpot));
        when(bookingSpotService.checkIfPreviouslyBooked(user.getId(), spotId, bookingSpot.getStartTime())).thenReturn(true);

        String result = bookingSpotController.submitBookingSpotForm(spotId, spotLocation, redirectAttributes, bookingSpot, session);

        verify(redirectAttributes).addFlashAttribute("errorMessage", "Please provide a valid duration.");
        assertEquals("redirect:/bookingSpot/", result);
    }
    
    @Test
    void testSubmitBookingSpotForm_parkingSpotOptional_isempty() {
        Long spotId = 1L;
        String spotLocation = "Central";
        BookingSpot bookingSpot = new BookingSpot();
        bookingSpot.setStartTime(new Date(System.currentTimeMillis() + 3600 * 1000)); // Future time
        bookingSpot.setDuration(6.0);

        User user = new User();
        user.setId(1L);
        user.setPhoneNumber("1234567890");

        ParkingSpot parkingSpot = new ParkingSpot();
        parkingSpot.setId(spotId);
        parkingSpot.setLocation(spotLocation);

        when(session.getAttribute("userId")).thenReturn(user.getId());
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(parkingSpotRepository.findById(spotId)).thenReturn(Optional.empty());
        when(bookingSpotService.checkIfPreviouslyBooked(user.getId(), spotId, bookingSpot.getStartTime())).thenReturn(true);
        String result = bookingSpotController.submitBookingSpotForm(spotId, spotLocation, redirectAttributes, bookingSpot, session);
        assertEquals("redirect:/bookingSpot/", result);
    }
    
    @Test
    void submitBookingSpotFormforNull() {
    	when(session.getAttribute("userId")).thenReturn(null);
    	String result=bookingSpotController.submitBookingSpotForm(null, null, redirectAttributes, null, session);
    	assertEquals("redirect:/login",result);
    }

    @Test
    void testSubmitBookingSpotForm_SpotAlreadyBooked() {
        Long spotId = 1L;
        String spotLocation = "Central";
        BookingSpot bookingSpot = new BookingSpot();
        bookingSpot.setStartTime(new Date(System.currentTimeMillis() + 3600 * 1000)); // Future time
        bookingSpot.setDuration(2.0);

        User user = new User();
        user.setId(1L);

        when(session.getAttribute("userId")).thenReturn(user.getId());
        when(bookingSpotService.checkIfPreviouslyBooked(user.getId(), spotId, bookingSpot.getStartTime())).thenReturn(false);

        String result = bookingSpotController.submitBookingSpotForm(spotId, spotLocation, redirectAttributes, bookingSpot, session);

        verify(redirectAttributes).addFlashAttribute("errorMessage", "It is already booked for the start time you have chosen. Please select another time or another spotLocation.");
        assertEquals("redirect:/bookingSpot/", result);
    }
    @Test
    void testGetPricePerHour_ValidSpot() {
        ParkingSpot parkingSpot = new ParkingSpot();
        parkingSpot.setPricePerHour(15.0);
        when(parkingSpotRepository.findById(1L)).thenReturn(Optional.of(parkingSpot));

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        Map<String, Object> response = bookingSpotController.getPricePerHour(1L, redirectAttributes);

        assertNotNull(response, "Response should not be null for a valid parking spot.");
        assertEquals(15.0, response.get("pricePerHour"), "Price per hour should match the parking spot's value.");
    }


    @Test
    void testGetPricePerHour_ParkingSpotExists() {
        // Arrange
        ParkingSpot parkingSpot = new ParkingSpot();
        parkingSpot.setPricePerHour(10.0);
        when(parkingSpotRepository.findById(1L)).thenReturn(Optional.of(parkingSpot));

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Act
        Map<String, Object> response = bookingSpotController.getPricePerHour(1L, redirectAttributes);

        // Assert
        assertNotNull(response, "Response should not be null when parking spot exists");
        assertEquals(10.0, response.get("pricePerHour"), "Price per hour should match the value set in ParkingSpot");
    }


    @Test
    void testGetPricePerHour_InvalidSpot() {
        // Arrange
        when(parkingSpotRepository.findById(2L)).thenReturn(Optional.empty());
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Act
        Map<String, Object> response = bookingSpotController.getPricePerHour(2L, redirectAttributes);

        // Assert
        assertNotNull(response, "Response should not be null for a non-existent parking spot.");
        assertTrue(response.containsKey("error"), "Response should contain an error key.");
        assertEquals("Parking spot not found for the given ID.", response.get("error"),
                "Error message should match the expected value.");
    }

    @Test
    void testGetPricePerHour_NullSpotId() {
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        assertThrows(IllegalArgumentException.class, () -> {
            bookingSpotController.getPricePerHour(null, redirectAttributes);
        }, "Null spotId should throw an exception.");
    }


    @Test
    void testGetPricePerHour_MultipleRequests() {
        ParkingSpot parkingSpot = new ParkingSpot();
        parkingSpot.setPricePerHour(30.0);
        when(parkingSpotRepository.findById(7L)).thenReturn(Optional.of(parkingSpot));

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        for (int i = 0; i < 5; i++) {
            Map<String, Object> response = bookingSpotController.getPricePerHour(7L, redirectAttributes);
            assertNotNull(response, "Response should not be null on request " + (i + 1));
            assertEquals(30.0, response.get("pricePerHour"), "Price per hour should be consistent on request " + (i + 1));
        }
    }

    @Test
    void testGetPricePerHour_FloatingPointPrice() {
        ParkingSpot parkingSpot = new ParkingSpot();
        parkingSpot.setPricePerHour(12.345);
        when(parkingSpotRepository.findById(6L)).thenReturn(Optional.of(parkingSpot));

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        Map<String, Object> response = bookingSpotController.getPricePerHour(6L, redirectAttributes);

        assertNotNull(response, "Response should not be null for a valid spot with a floating point price.");
        assertEquals(12.345, response.get("pricePerHour"), "Price per hour should match the floating point value.");
    }

    @Test
    void testGetPricePerHour_NullRedirectAttributes() {
        ParkingSpot parkingSpot = new ParkingSpot();
        parkingSpot.setPricePerHour(20.0);
        when(parkingSpotRepository.findById(5L)).thenReturn(Optional.of(parkingSpot));

        Map<String, Object> response = bookingSpotController.getPricePerHour(5L, null);

        assertNotNull(response, "Response should not be null even if RedirectAttributes is null.");
        assertEquals(20.0, response.get("pricePerHour"), "Price per hour should match the parking spot value.");
    }

    @Test
    void testGetPricePerHour_LargePriceSpot() {
        ParkingSpot parkingSpot = new ParkingSpot();
        parkingSpot.setPricePerHour(9999.99);
        when(parkingSpotRepository.findById(4L)).thenReturn(Optional.of(parkingSpot));

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        Map<String, Object> response = bookingSpotController.getPricePerHour(4L, redirectAttributes);

        assertNotNull(response, "Response should not be null for a valid spot with a large price.");
        assertEquals(9999.99, response.get("pricePerHour"), "Price per hour should match the large price.");
    }

    @Test
    void testGetPricePerHour_ZeroPriceSpot() {
        ParkingSpot parkingSpot = new ParkingSpot();
        parkingSpot.setPricePerHour(0.0);
        when(parkingSpotRepository.findById(3L)).thenReturn(Optional.of(parkingSpot));

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        Map<String, Object> response = bookingSpotController.getPricePerHour(3L, redirectAttributes);

        assertNotNull(response, "Response should not be null for a valid spot with zero price.");
        assertEquals(0.0, response.get("pricePerHour"), "Price per hour should be zero.");
    }

    @Test
    void testGetPricePerHour_DatabaseException() {
        when(parkingSpotRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        assertThrows(RuntimeException.class, () -> {
            bookingSpotController.getPricePerHour(1L, redirectAttributes);
        }, "Should throw an exception when the repository encounters an error.");
    }

}