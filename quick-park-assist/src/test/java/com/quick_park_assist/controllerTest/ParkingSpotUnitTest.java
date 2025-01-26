package com.quick_park_assist.controllerTest;
import com.quick_park_assist.controller.ParkingSpotController;
import com.quick_park_assist.entity.ParkingSpot;
import com.quick_park_assist.entity.User;
import com.quick_park_assist.repository.ParkingSpotRepository;
import com.quick_park_assist.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ParkingSpotUnitTest {

    @InjectMocks
    private ParkingSpotController parkingSpotController;  // Assuming this is the controller name

    @Mock
    private HttpSession session;

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
}