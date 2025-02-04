package com.quick_park_assist.controllerTest;

import com.quick_park_assist.controller.ParkingSpotController;
import com.quick_park_assist.entity.ParkingSpot;
import com.quick_park_assist.entity.User;
import com.quick_park_assist.repository.ParkingSpotRepository;
import com.quick_park_assist.repository.UserRepository;
import com.quick_park_assist.service.IUpdateParkingSpotService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ParkingSpotUnitTest {

    @InjectMocks
    private ParkingSpotController parkingSpotController;  // Assuming this is the controller name

    @Mock
    private HttpSession session;

    @Mock
    private IUpdateParkingSpotService updateParkingSpotService;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    //Test 1

    @Test
    public void testShowAddSpotForm_UserNotLoggedIn_ShouldRedirectToLogin() {
        // Arrange
        when(session.getAttribute("userId")).thenReturn(null);  // No user in session

        // Act
        String viewName = parkingSpotController.showAddSpotForm(session, model);

        // Assert
        assertEquals("redirect:/login", viewName);
    }

    //Test 2

    @Test
    public void testShowAddSpotForm_UserIsSpotOwner_ShouldReturnAddParkingSpotView() {
        // Arrange
        when(session.getAttribute("userId")).thenReturn(1L); // Logged in user
        when(session.getAttribute("userType")).thenReturn("SPOT_OWNER"); // User type is spot owner

        // Act
        String viewName = parkingSpotController.showAddSpotForm(session, model);

        // Assert
        assertEquals("AddParkingSpot", viewName);  // Should return the AddParkingSpot view
        verify(model).addAttribute(eq("parkingSpot"), any(ParkingSpot.class)); // Verify model is populated with a new ParkingSpot
    }

    //Test 3

    @Test
    public void testShowAddSpotForm_UserIsNotSpotOwner_ShouldRedirectToSearch() {
        // Arrange
        when(session.getAttribute("userId")).thenReturn(1L);  // Logged in user
        when(session.getAttribute("userType")).thenReturn("OTHER_TYPE"); // User is not a spot owner

        // Act
        String viewName = parkingSpotController.showAddSpotForm(session, model);

        // Assert
        assertEquals("redirect:/smart-spots/search", viewName);  // Should redirect to the search page
    }




    @Mock
    private UserRepository userRepository;

    @Mock
    private ParkingSpotRepository parkingSpotRepository;

    private Long loggedInUserId;
    private User user;
    private ParkingSpot parkingSpot;

    @BeforeEach
    public void setUpAdd() {
        MockitoAnnotations.openMocks(this);  // Initialize mocks
        loggedInUserId = 1L;  // Sample logged-in user ID

        // Creating a mock user
        user = new User();
        user.setId(loggedInUserId);

        // Creating a mock parking spot
        parkingSpot = new ParkingSpot();
        parkingSpot.setSpotLocation("Location1");
        parkingSpot.setLocation("Address1");
    }

    //Test 4

    @Test
    public void testAddSpot_UserNotLoggedIn_ShouldRedirectToLogin() {


        ParkingSpot parkingSpot;

        parkingSpot = new ParkingSpot();
        parkingSpot.setSpotLocation("Location1");
        parkingSpot.setLocation("Address1");

        // Arrange: Mock session to return null for "userId"
        when(session.getAttribute("userId")).thenReturn(null);

        // Act: Call the controller method
        String viewName = parkingSpotController.addSpot(session, parkingSpot, model, redirectAttributes);

        // Assert: Verify that the user is redirected to login
        assertEquals("redirect:/login", viewName);
    }

    //Test 5

    @Test
    public void testAddSpot_UserExists_SpotAlreadyOwned_ShouldRedirectWithError() {
        // Arrange: Mock session to return loggedInUserId
        when(session.getAttribute("userId")).thenReturn(loggedInUserId);

        // Mock user repository to return the mock user
        when(userRepository.findById(loggedInUserId)).thenReturn(Optional.of(user));

        // Mock parkingSpotRepository to indicate the parking spot already exists
        when(parkingSpotRepository.existsParkingSpotBySpotLocationIgnoreCaseAndLocationIgnoreCase("Location1", "Address1"))
                .thenReturn(true);

        // Act: Call the controller method
        String viewName = parkingSpotController.addSpot(session, parkingSpot, model, redirectAttributes);

        // Assert: Verify the redirect with the error message
        assertEquals("redirect:/smart-spots/add-spot", viewName);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "The Spot is Already Owned, Please Choose another Spot Location.");
    }

    //Test 6

    @Test
    public void testAddSpot_SuccessfulSpotAddition_ShouldRedirectWithSuccessMessage() {
        // Arrange: Mock session to return loggedInUserId
        when(session.getAttribute("userId")).thenReturn(loggedInUserId);

        // Mock user repository to return the mock user
        when(userRepository.findById(loggedInUserId)).thenReturn(Optional.of(user));

        // Mock parkingSpotRepository to indicate the parking spot does not already exist
        when(parkingSpotRepository.existsParkingSpotBySpotLocationIgnoreCaseAndLocationIgnoreCase("Location1", "Address1"))
                .thenReturn(false);

        // Act: Call the controller method
        String viewName = parkingSpotController.addSpot(session, parkingSpot, model, redirectAttributes);

        // Assert: Verify the redirect with the success message
        assertEquals("redirect:/smart-spots/add-spot", viewName);
        verify(parkingSpotRepository).save(parkingSpot);  // Verify that the parking spot was saved
        verify(redirectAttributes).addFlashAttribute("successMessage", "Your new Parking Spot is now Added!");
    }
    @Test
    public void testSearchLocations_WithQuery_ShouldReturnMatchingSpots() {
        // Arrange
        String query = "Location1";
        List<ParkingSpot> expectedSpots = List.of(new ParkingSpot(), new ParkingSpot());
        when(parkingSpotRepository.findByAvailabilityIgnoreCaseAndSpotLocationContainingIgnoreCaseOrLocationContainingIgnoreCaseAndSpotTypeNot(
                "Available", query, query, "EV_SPOT")).thenReturn(expectedSpots);

        // Act
        List<ParkingSpot> result = parkingSpotController.searchLocations(query);

        // Assert
        assertEquals(expectedSpots.size(), result.size());
        verify(parkingSpotRepository).findByAvailabilityIgnoreCaseAndSpotLocationContainingIgnoreCaseOrLocationContainingIgnoreCaseAndSpotTypeNot(
                "Available", query, query, "EV_SPOT");
    }

    @Test
    void testSearchLocations_WithoutQuery_ShouldReturnEmptyList() {
        // Act
        List<ParkingSpot> result = parkingSpotController.searchLocations("");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testShowSearchParkingSpotsForm_UserNotLoggedIn_ShouldRedirectToLogin() {
        // Arrange
        when(session.getAttribute("userId")).thenReturn(null);

        // Act
        String viewName = parkingSpotController.showSearchParkingSpotsForm("Location1", "Available", model, session, redirectAttributes);

        // Assert
        assertEquals("redirect:/login", viewName);
    }

    @Test
    void testShowSearchParkingSpotsForm_NoLocation_ShouldRedirectWithError() {
        // Arrange
        when(session.getAttribute("userId")).thenReturn(1L);

        // Act
        String viewName = parkingSpotController.showSearchParkingSpotsForm("", "Available", model, session, redirectAttributes);

        // Assert
        assertEquals("SearchParkingSpot", viewName);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Please enter a location to search.");
    }

    @Test
    void testShowSearchParkingSpotsForm_ValidSearchResults_ShouldReturnSearchView() {
        // Arrange
        String location = "Location1";
        when(session.getAttribute("userId")).thenReturn(1L);
        List<ParkingSpot> parkingSpots = List.of(new ParkingSpot());
        when(parkingSpotRepository.findByLocationContainingIgnoreCaseAndAvailabilityIgnoreCaseAndSpotTypeNot(
                location, "Available", "EV_SPOT")).thenReturn(parkingSpots);

        // Act
        String viewName = parkingSpotController.showSearchParkingSpotsForm(location, "Available", model, session, redirectAttributes);

        // Assert
        assertEquals("SearchParkingSpot", viewName);
        verify(model).addAttribute("parkingSpots", parkingSpots);
    }

    @Test
    void testShowSearchParkingSpotsForm_SearchWithAvailabilityAll_ShouldReturnSearchView() {
        // Arrange
        String location = "Location1";
        String availability = "all";
        when(session.getAttribute("userId")).thenReturn(1L);
        List<ParkingSpot> parkingSpots = List.of(new ParkingSpot(), new ParkingSpot());
        when(parkingSpotRepository.findByLocationContainingIgnoreCaseAndSpotTypeNot(location, "EV_SPOT"))
                .thenReturn(parkingSpots);

        // Act
        String viewName = parkingSpotController.showSearchParkingSpotsForm(location, availability, model, session, redirectAttributes);

        // Assert
        assertEquals("SearchParkingSpot", viewName);
        verify(model).addAttribute("parkingSpots", parkingSpots);
    }

    @Test
    void testShowSearchParkingSpotsForm_NoResultsFound_ShouldAddErrorMessage() {
        // Arrange
        String location = "Location1";
        String availability = "Available";
        when(session.getAttribute("userId")).thenReturn(1L);
        when(parkingSpotRepository.findByLocationContainingIgnoreCaseAndAvailabilityIgnoreCaseAndSpotTypeNot(
                location, availability, "EV_SPOT")).thenReturn(List.of());

        // Act
        String viewName = parkingSpotController.showSearchParkingSpotsForm(location, availability, model, session, redirectAttributes);

        // Assert
        assertEquals("SearchParkingSpot", viewName);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "No parking spots found for the given location and availability.");
    }

    @Test
    void testShowSearchParkingSpotsForm_ExceptionThrown_ShouldAddErrorMessage() {
        // Arrange
        String location = "Location1";
        String availability = "Available";
        when(session.getAttribute("userId")).thenReturn(1L);
        when(parkingSpotRepository.findByLocationContainingIgnoreCaseAndAvailabilityIgnoreCaseAndSpotTypeNot(
                location, availability, "EV_SPOT")).thenThrow(new RuntimeException("Database error"));

        // Act
        String viewName = parkingSpotController.showSearchParkingSpotsForm(location, availability, model, session, redirectAttributes);

        // Assert
        assertEquals("SearchParkingSpot", viewName);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "An error occurred while searching for parking spots.");
    }

    @Test
    void testGetParkingSpots_UserNotLoggedIn_ShouldRedirectToLogin() {
        // Arrange
        when(session.getAttribute("userId")).thenReturn(null);

        // Act
        String viewName = parkingSpotController.getParkingSpots(model, session);

        // Assert
        assertEquals("redirect:/login", viewName);
    }

    @Test
    void testGetParkingSpots_UserNotSpotOwner_ShouldRedirectToSearch() {
        // Arrange
        when(session.getAttribute("userId")).thenReturn(1L);
        when(session.getAttribute("userType")).thenReturn("OTHER_TYPE");

        // Act
        String viewName = parkingSpotController.getParkingSpots(model, session);

        // Assert
        assertEquals("redirect:/smart-spots/search", viewName);
    }

    @Test
    void testGetParkingSpots_UserIsSpotOwner_ShouldReturnEditParkingSpotView() {
        // Arrange
        when(session.getAttribute("userId")).thenReturn(1L);
        when(session.getAttribute("userType")).thenReturn("SPOT_OWNER");
        List<ParkingSpot> parkingSpots = List.of(new ParkingSpot(), new ParkingSpot());
        when(updateParkingSpotService.getParkingSpotsForLoggedInUser(1L)).thenReturn(parkingSpots);
        // Act
        String viewName = parkingSpotController.getParkingSpots(model, session);

        // Assert
        assertEquals("EditParkingSpot", viewName);
        verify(model).addAttribute("parkingSpots", parkingSpots);
    }

    @Test
    public void testRemoveParkingSpot_UserNotLoggedIn_ShouldRedirectToLogin() {
        when(session.getAttribute("userId")).thenReturn(null);

        String viewName = parkingSpotController.removeParkingSpot(1L, model, session, redirectAttributes);

        assertEquals("redirect:/login", viewName);
    }

    @Test
    public void testRemoveParkingSpot_SuccessfulRemoval_ShouldRedirectWithSuccessMessage() {
        when(session.getAttribute("userId")).thenReturn(loggedInUserId);
        ParkingSpot mockSpot = new ParkingSpot();
        mockSpot.setId(1L);
        when(parkingSpotRepository.findById(1L)).thenReturn(Optional.of(mockSpot));

        String viewName = parkingSpotController.removeParkingSpot(1L, model, session, redirectAttributes);

        assertEquals("redirect:/smart-spots/remove", viewName);
        verify(parkingSpotRepository).delete(mockSpot);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Parking spot removed successfully.");
    }

    @Test
    public void testRemoveParkingSpot_ParkingSpotNotFound_ShouldRedirectWithErrorMessage() {
        when(session.getAttribute("userId")).thenReturn(loggedInUserId);
        when(parkingSpotRepository.findById(1L)).thenReturn(Optional.empty());

        String viewName = parkingSpotController.removeParkingSpot(1L, model, session, redirectAttributes);

        assertEquals("redirect:/smart-spots/remove", viewName);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Parking spot not found.");
    }
    @Test
    public void testShowRemoveParkingSpot_UserNotLoggedIn_ShouldRedirectToLogin() {
        when(session.getAttribute("userId")).thenReturn(null);

        String viewName = parkingSpotController.showRemoveParkingSpot(model, session, redirectAttributes);

        assertEquals("redirect:/login", viewName);
    }

    @Test
    public void testShowRemoveParkingSpot_UserIsSpotOwner_ShouldReturnRemoveParkingSpotView() {
        when(session.getAttribute("userId")).thenReturn(loggedInUserId);
        when(session.getAttribute("userType")).thenReturn("SPOT_OWNER");
        List<ParkingSpot> parkingSpots = List.of(new ParkingSpot(), new ParkingSpot());
        when(updateParkingSpotService.getParkingSpotsForLoggedInUser(loggedInUserId)).thenReturn(parkingSpots);

        String viewName = parkingSpotController.showRemoveParkingSpot(model, session, redirectAttributes);

        assertEquals("RemoveParkingSpot", viewName);
        verify(model).addAttribute("parkingSpots", parkingSpots);
    }

    @Test
    public void testShowRemoveParkingSpot_NoParkingSpots_ShouldRedirectWithErrorMessage() {
        when(session.getAttribute("userId")).thenReturn(loggedInUserId);
        when(session.getAttribute("userType")).thenReturn("SPOT_OWNER");
        when(updateParkingSpotService.getParkingSpotsForLoggedInUser(loggedInUserId)).thenReturn(List.of());

        String viewName = parkingSpotController.showRemoveParkingSpot(model, session, redirectAttributes);

        assertEquals("RemoveParkingSpot", viewName);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Parking spot not found.");
    }

    @Test
    public void testUpdateParkingSpot_SuccessfulUpdate_ShouldRedirectWithSuccessMessage() {
        // Arrange
        Long spotId = 1L;
        String availability = "Available";
        Double pricePerHour = 20.0;
        String spotType = "Standard";
        String additionalInstructions = "No instructions";

        when(updateParkingSpotService.updateParkingSpot(spotId, availability, pricePerHour, spotType, additionalInstructions))
                .thenReturn(true);

        // Act
        String viewName = parkingSpotController.updateParkingSpot(
                spotId, availability, pricePerHour, spotType, additionalInstructions, redirectAttributes, model);

        // Assert
        assertEquals("redirect:/smart-spots/update-spot", viewName);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Parking spot updated successfully!");
    }}