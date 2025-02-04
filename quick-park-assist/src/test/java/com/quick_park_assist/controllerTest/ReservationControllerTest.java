package com.quick_park_assist.controllerTest;

import com.quick_park_assist.controller.ReservationController;
import com.quick_park_assist.entity.ParkingSpot;
import com.quick_park_assist.entity.Reservation;
import com.quick_park_assist.entity.User;
import com.quick_park_assist.repository.ParkingSpotRepository;
import com.quick_park_assist.service.IReservationService;
import com.quick_park_assist.repository.VehicleRepository;
import com.quick_park_assist.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.web.servlet.MockMvc;

import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.text.SimpleDateFormat;

@SpringBootTest
@AutoConfigureMockMvc  // Add AutoConfigureMockMvc to enable MockMvc in your test
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private IReservationService reservationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private ParkingSpotRepository parkingSpotRepository;

    @InjectMocks
    private ReservationController reservationController;
    private final Long USER_ID = 1L;
    private final String EV_SPOT = "101";
    private final String EV_VEHICLE = "MH12EV001";
    private static final Long MOCK_USER_ID = 123L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
   

    


    @Test
    void testEditReservation_UnauthenticatedUser() throws Exception {
        mockMvc.perform(get("/ev-charging/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
    @Test
    void testUpdateReservation_InvalidDate() throws Exception {
        Long userId = 1L;
        Long reservationId = 1L;
        String startTime = "2025-02-01T14:00";
        String vehicleNumber = "EV123";

        given(reservationService.updateSpotDetails(reservationId, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(startTime), vehicleNumber)).willReturn(false);

        mockMvc.perform(post("/ev-charging/update-reservation")
                .sessionAttr("userId", userId)
                .param("id", String.valueOf(reservationId))
                .param("startTime", startTime)
                .param("vehicleNumber", vehicleNumber))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ev-charging/edit"));
    }


    @Test
    void testDeleteReservation_Invalid() throws Exception {
        Long reservationId = 1L;

        given(reservationService.deleteReservationById(reservationId)).willReturn(false);

        mockMvc.perform(post("/ev-charging/delete/{id}", reservationId))
                .andExpect(status().isBadRequest()); // Expecting 400 Bad Request
    }
    
   

    
    @Test
    void testEditReservation_InvalidDateFormat() throws Exception {
        Long userId = 1L;
        Long reservationId = 1L;
        String invalidDate = "invalid-date";
        String vehicleNumber = "EV123";

        mockMvc.perform(post("/ev-charging/update-reservation")
                .sessionAttr("userId", userId)
                .param("id", String.valueOf(reservationId))
                .param("startTime", invalidDate)
                .param("vehicleNumber", vehicleNumber))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ev-charging/edit"))
                .andExpect(flash().attribute("errorMessage", "Exception Invalid date format. Please use the correct format."));
    }
  
    @Test
    void testShowDeleteReservationForm_UnauthenticatedUser() throws Exception {
        mockMvc.perform(get("/ev-charging/delete-form"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
    @Test
    void testShowReservationList_NoReservations() throws Exception {
        Long userId = 1L;
        given(reservationService.getReservationsByUserId(userId)).willReturn(Collections.emptyList());

        mockMvc.perform(get("/ev-charging/list").sessionAttr("userId", userId))
                .andExpect(status().isOk())
                .andExpect(view().name("ViewReservations"));
                
    }

  
    @Test
    void testAddReservationForm_NoSession() throws Exception {
        mockMvc.perform(get("/ev-charging/add"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void testDeleteReservation_InvalidHttpMethod() throws Exception {
        mockMvc.perform(get("/ev-charging/delete/{id}", 1L))
                .andExpect(status().isMethodNotAllowed());
    }

    

    

    @Test
    void testDeleteReservation_EmptyId() throws Exception {
        mockMvc.perform(post("/ev-charging/delete/"))
                .andExpect(status().isNotFound());
    }



    @Test
    void testDeleteReservation_LargeId() throws Exception {
        Long largeReservationId = Long.MAX_VALUE;

        // Mock the service to return false for a large ID
        given(reservationService.deleteReservationById(largeReservationId)).willReturn(false);

        // Perform the request and add assertions
        mockMvc.perform(post("/ev-charging/delete/{id}", largeReservationId)
                        .param("id", String.valueOf(largeReservationId))) // Pass ID as a parameter
                .andExpect(status().is3xxRedirection()) // Expect a redirect response
                .andExpect(redirectedUrl("/ev-charging/delete-form")) // Verify redirection
                .andExpect(flash().attribute("errorMessage", "Reservation Couldn't be Cancelled")); // Verify flash message
    }
    @Test
    void testAddReservation_NoEVRegistered() throws Exception {
        Long userId = 3L;

        // Simulate no EV registered for the user
        given(vehicleRepository.existsElectricVehicleByUserId(userId)).willReturn(false);

        mockMvc.perform(get("/ev-charging/add").sessionAttr("userId", userId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/vehicles/add"))
                .andExpect(flash().attribute("errorMessage", "You currently Don't have an EV to Reserve a spot"));
    }


    @Test
    void testDeleteReservation_UnauthorizedUser() throws Exception {
        Long unauthorizedReservationId = 999L;

        // Simulate unauthorized deletion attempt
        given(reservationService.deleteReservationById(unauthorizedReservationId)).willReturn(false);

        mockMvc.perform(post("/ev-charging/delete/{id}", unauthorizedReservationId)
                        .param("id", String.valueOf(unauthorizedReservationId))) // Ensure the ID is passed correctly
                .andExpect(status().is3xxRedirection()) // Expect a redirection on failure
                .andExpect(redirectedUrl("/ev-charging/delete-form")) // Verify redirection to delete-form
                .andExpect(flash().attribute("errorMessage", "Reservation Couldn't be Cancelled")); // Ensure the correct error message is flashed
    }

    @Test
    void testListReservations_MultipleUsers() throws Exception {
        Long userId1 = 1L;
        Long userId2 = 2L;

        // Simulate reservations for two users
        given(reservationService.getReservationsByUserId(userId1)).willReturn(Collections.singletonList(new Reservation()));
        given(reservationService.getReservationsByUserId(userId2)).willReturn(Collections.emptyList());

        mockMvc.perform(get("/ev-charging/list").sessionAttr("userId", userId1))
                .andExpect(status().isOk())
                .andExpect(view().name("ViewReservations"))
                .andExpect(model().attributeExists("reservations"));

        mockMvc.perform(get("/ev-charging/list").sessionAttr("userId", userId2))
                .andExpect(status().isOk())
                .andExpect(view().name("ViewReservations"))
                .andExpect(model().attribute("reservations", Collections.emptyList()));
    }

    @Test
    void testListReservations_InvalidSession() throws Exception {
        mockMvc.perform(get("/ev-charging/list"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void testDeleteReservation_NotSuccessfulCancellation() throws Exception {
        Long reservationId = 2L;

        // Simulate successful deletion
        given(reservationService.deleteReservationById(reservationId)).willReturn(false);

        mockMvc.perform(post("/ev-charging/delete/{id}", reservationId)
                        .param("id", String.valueOf(reservationId))) // Ensure the ID is passed correctly
                .andExpect(status().is3xxRedirection()) // Verify that the response is a redirection
                .andExpect(redirectedUrl("/ev-charging/delete-form")) // Verify the redirect URL
                .andExpect(flash().attribute("errorMessage", "Reservation Couldn't be Cancelled")); // Verify the flash attribute
    }


    @Test
    void testListReservationsUserNotLoggedIn() {
        when(session.getAttribute("userId")).thenReturn(null);
        String result = reservationController.listReservations(session, model);
        assertEquals("redirect:/login", result);
    }

    @Test
    void testListReservationsUserLoggedIn() {
        when(session.getAttribute("userId")).thenReturn(1L);
        when(reservationService.getReservationsByUserId(1L)).thenReturn(Arrays.asList(new Reservation()));
        String result = reservationController.listReservations(session, model);
        assertEquals("ViewReservations", result);
    }

    @Test
    void testAddReservationFormUserNotLoggedIn() {
        when(session.getAttribute("userId")).thenReturn(null);
        String result = reservationController.addReservationForm(session, model, redirectAttributes);
        assertEquals("redirect:/login", result);
    }

    @Test
    void testAddReservationFormUserHasNoEV() {
        when(session.getAttribute("userId")).thenReturn(1L);
        when(vehicleRepository.existsElectricVehicleByUserId(1L)).thenReturn(false);
        String result = reservationController.addReservationForm(session, model, redirectAttributes);
        assertEquals("redirect:/vehicles/add", result);
    }

    @Test
    void testEditFormUserNotLoggedIn() {
        when(session.getAttribute("userId")).thenReturn(null);
        String result = reservationController.editForm(session, model);
        assertEquals("redirect:/login", result);
    }

    @Test
    void testEditFormUserLoggedIn() {
        when(session.getAttribute("userId")).thenReturn(1L);
        when(reservationService.getReservationsByUserId(1L)).thenReturn(Arrays.asList(new Reservation()));
        String result = reservationController.editForm(session, model);
        assertEquals("EditReservation", result);
    }

    @Test
    void testDeleteReservationSuccess() {
        when(reservationService.deleteReservationById(1L)).thenReturn(true);
        String result = reservationController.deleteReservation(1L, model, redirectAttributes);
        assertEquals("redirect:/ev-charging/delete-form", result);
    }

    @Test
    void testDeleteReservationFailure() {
        when(reservationService.deleteReservationById(1L)).thenReturn(false);
        String result = reservationController.deleteReservation(1L, model, redirectAttributes);
        assertEquals("redirect:/ev-charging/delete-form", result);
    }


    @Test
    void testDeleteReservationWithNonExistentId() {
        when(reservationService.deleteReservationById(999L)).thenReturn(false);
        String result = reservationController.deleteReservation(999L, model, redirectAttributes);
        assertEquals("redirect:/ev-charging/delete-form", result);
    }


    @Test
    void testShowDeleteFormUserLoggedIn() {
        when(session.getAttribute("userId")).thenReturn(1L);
        when(reservationService.getReservationsByUserId(1L)).thenReturn(Arrays.asList(new Reservation()));
        String result = reservationController.showDeleteForm(session, model);
        assertEquals("CancelReservation", result);
    }

    @Test
    void testReservationServiceCalledCorrectly() {
        when(session.getAttribute("userId")).thenReturn(1L);
        reservationController.listReservations(session, model);
        verify(reservationService).getReservationsByUserId(1L);
    }

    @Test
    void testModelReceivesReservationList() {
        when(session.getAttribute("userId")).thenReturn(1L);
        when(reservationService.getReservationsByUserId(1L)).thenReturn(Arrays.asList(new Reservation()));
        reservationController.listReservations(session, model);
        verify(model).addAttribute(eq("reservations"), anyList());
    }

    @Test
    void testInvalidSessionHandlingForDeleteForm() {
        when(session.getAttribute("userId")).thenReturn(null);
        String result = reservationController.showDeleteForm(session, model);
        assertEquals("redirect:/login", result);
    }


    @Test
    void testDeleteReservationByInvalidId() {
        when(reservationService.deleteReservationById(2L)).thenReturn(false);
        String result = reservationController.deleteReservation(2L, model, redirectAttributes);
        assertEquals("redirect:/ev-charging/delete-form", result);
    }


    @Test
    void testReservationTimeExactlyAtPresent() throws Exception {
        Long userId = 1L;
        Date now = new Date();
        now = new Date((now.getTime() / 1000) * 1000); // Truncate milliseconds for consistency

        when(session.getAttribute("userId")).thenReturn(userId);
        when(vehicleRepository.existsVehicleByVehicleNumberAndUserIdAndEvTrue(eq("EV123"), eq(userId))).thenReturn(true);
        when(reservationService.isTimeSlotAvailable(eq(now), eq("EV123"))).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        mockMvc.perform(post("/ev-charging/add-reservation")
                        .sessionAttr("userId", userId)
                        .param("evVehicle", "EV123")
                        .param("evSpot", "1")
                        .param("reservationTime", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(now)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ev-charging/add"));
    }

    @Test
    void testDatabaseUserLookupFailure() throws Exception {
        Long userId = 1L;
        Date futureDate = new Date(System.currentTimeMillis() + 10000);

        when(session.getAttribute("userId")).thenReturn(userId);
        when(vehicleRepository.existsVehicleByVehicleNumberAndUserIdAndEvTrue(anyString(), eq(userId))).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Reservation reservation = new Reservation();
        reservation.setReservationTime(futureDate);

        mockMvc.perform(post("/ev-charging/add-reservation")
                        .sessionAttr("userId", userId)
                        .param("evVehicle", "EV123")
                        .param("evSpot", "1")
                        .flashAttr("reservation", reservation))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ev-charging/add"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    void testParkingSpotWithMissingLocation() throws Exception {
        Long userId = 1L;
        Date futureDate = new Date(System.currentTimeMillis() + 10000);
        ParkingSpot parkingSpot = new ParkingSpot();
        parkingSpot.setLocation(null);
        parkingSpot.setSpotLocation(null);

        when(session.getAttribute("userId")).thenReturn(userId);
        when(parkingSpotRepository.findById(1L)).thenReturn(Optional.of(parkingSpot));

        Reservation reservation = new Reservation();
        reservation.setReservationTime(futureDate);

        mockMvc.perform(post("/ev-charging/add-reservation")
                        .sessionAttr("userId", userId)
                        .param("evVehicle", "EV123")
                        .param("evSpot", "1")
                        .flashAttr("reservation", reservation))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ev-charging/add"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    void testReservationWithNullFields() throws Exception {
        Long userId = 1L;
        when(session.getAttribute("userId")).thenReturn(userId);

        mockMvc.perform(post("/ev-charging/add-reservation")
                        .sessionAttr("userId", userId)
                        .param("evVehicle", "")
                        .param("evSpot", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ev-charging/add"))
                .andExpect(flash().attribute("errorMessage", "Invalid vehicle or spot selection."));
    }


    @Test
    void testInvalidReservationIdForUpdate() throws Exception {
        Long userId = 1L;
        when(session.getAttribute("userId")).thenReturn(userId);
        when(reservationService.updateSpotDetails(anyLong(), any(), anyString())).thenReturn(false);

        mockMvc.perform(post("/ev-charging/update-reservation")
                        .sessionAttr("userId", userId)
                        .param("id", "9999")
                        .param("startTime", "2025-02-01T12:00")
                        .param("vehicleNumber", "EV123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ev-charging/edit"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    void testUnauthorizedUpdateAttempt() throws Exception {
        Long userId = 1L;
        when(session.getAttribute("userId")).thenReturn(userId);
        when(vehicleRepository.existsByVehicleNumberAndUserIdAndEvTrue("EV123", userId)).thenReturn(false);

        mockMvc.perform(post("/ev-charging/update-reservation")
                        .sessionAttr("userId", userId)
                        .param("id", "1")
                        .param("startTime", "2025-02-01T12:00")
                        .param("vehicleNumber", "EV123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ev-charging/edit"))
                .andExpect(flash().attribute("errorMessage", "The Vehicle is not an EV"));
    }
    @Test
    void testListReservations_ValidUser() {
        when(session.getAttribute("userId")).thenReturn(USER_ID);
        List<Reservation> reservations = List.of(new Reservation());
        when(reservationService.getReservationsByUserId(USER_ID)).thenReturn(reservations);

        String viewName = reservationController.listReservations(session, model);

        verify(model).addAttribute("reservations", reservations);
        assertEquals("ViewReservations", viewName);
    }

    @Test
    void testListReservations_InvalidUser() {
        when(session.getAttribute("userId")).thenReturn(null);
        String viewName = reservationController.listReservations(session, model);
        assertEquals("redirect:/login", viewName);
    }

    @Test
    void testAddReservationForm_ValidUserWithEV() {
        when(session.getAttribute("userId")).thenReturn(USER_ID);
        when(vehicleRepository.existsElectricVehicleByUserId(USER_ID)).thenReturn(true);
        List<ParkingSpot> evSpots = List.of(new ParkingSpot());
        List<String> evVehicles = List.of(EV_VEHICLE);

        when(parkingSpotRepository.findBySpotType("EV_SPOT")).thenReturn(evSpots);
        when(vehicleRepository.findEVVehicles(USER_ID)).thenReturn(evVehicles);

        String viewName = reservationController.addReservationForm(session, model, redirectAttributes);

        verify(model).addAttribute("evSpots", evSpots);
        verify(model).addAttribute("evVehicles", evVehicles);
        assertEquals("addReservation", viewName);
    }

    @Test
    void testAddReservationForm_NoEVVehicles() {
        when(session.getAttribute("userId")).thenReturn(USER_ID);
        when(vehicleRepository.existsElectricVehicleByUserId(USER_ID)).thenReturn(false);

        String viewName = reservationController.addReservationForm(session, model, redirectAttributes);

        verify(redirectAttributes).addFlashAttribute("errorMessage", "You currently Don't have an EV to Reserve a spot");
        assertEquals("redirect:/vehicles/add", viewName);
    }

//    @Test
//    void testAddReservation_ValidInput() throws Exception {
//        when(session.getAttribute("userId")).thenReturn(USER_ID);
//        Reservation reservation = new Reservation();
//        reservation.setReservationTime(new Date(System.currentTimeMillis() + 3600000));
//        reservation.setVehicleNumber(EV_VEHICLE);
//
//        when(vehicleRepository.existsVehicleByVehicleNumberAndUserIdAndEvTrue(EV_VEHICLE, USER_ID)).thenReturn(true);
//        when(reservationService.isTimeSlotAvailable(reservation.getReservationTime(), EV_VEHICLE)).thenReturn(true);
//        User user = new User();
//        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
//
//        String viewName = reservationController.addReservation(EV_VEHICLE, EV_SPOT, session, reservation, redirectAttributes);
//
//        
//        verify(redirectAttributes).addFlashAttribute("successMessage", "Reservation is Successful!");
//        assertEquals("redirect:/ev-charging/add", viewName);
//    }

    @Test
    void testAddReservation_InvalidTimeSlot() throws Exception {
        when(session.getAttribute("userId")).thenReturn(USER_ID);
        Reservation reservation = new Reservation();
        reservation.setReservationTime(new Date(System.currentTimeMillis() - 3600000));
        reservation.setVehicleNumber(EV_VEHICLE);

        String viewName = reservationController.addReservation(EV_VEHICLE, EV_SPOT, session, reservation, redirectAttributes);

        verify(redirectAttributes).addFlashAttribute("errorMessage", "Please Choose Correct Date and time");
        assertEquals("redirect:/ev-charging/add", viewName);
    }

    @Test
    void testUpdateSpotDetails_ValidUpdate() throws Exception {
        when(session.getAttribute("userId")).thenReturn(USER_ID);
        String startTimeStr = "2025-04-30T10:00";
        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Date startTime = dateTimeFormatter.parse(startTimeStr);

        when(vehicleRepository.existsByVehicleNumberAndUserIdAndEvTrue(EV_VEHICLE, USER_ID)).thenReturn(true);
        when(reservationService.updateSpotDetails(1L, startTime, EV_VEHICLE)).thenReturn(true);

        String viewName = reservationController.updateSpotDetails(1L, startTimeStr, EV_VEHICLE, redirectAttributes, session, model);

        verify(redirectAttributes).addFlashAttribute("successMessage", "Booking updated successfully!");
        assertEquals("redirect:/ev-charging/edit", viewName);
    }

    @Test
    void testUpdateSpotDetails_InvalidDateFormat() {
        when(session.getAttribute("userId")).thenReturn(USER_ID);
        String startTimeStr = "invalid-date";

        String viewName = reservationController.updateSpotDetails(1L, startTimeStr, EV_VEHICLE, redirectAttributes, session, model);

        verify(redirectAttributes).addFlashAttribute("errorMessage", "Exception Invalid date format. Please use the correct format.");
        assertEquals("redirect:/ev-charging/edit", viewName);
    }

    @Test
    void testDeleteReservation_Success() {
        when(reservationService.deleteReservationById(1L)).thenReturn(true);

        String viewName = reservationController.deleteReservation(1L, model, redirectAttributes);

        verify(redirectAttributes).addFlashAttribute("successMessage", "Reservation Successfully Cancelled");
        assertEquals("redirect:/ev-charging/delete-form", viewName);
    }

    @Test
    void testDeleteReservation_Failure() {
        when(reservationService.deleteReservationById(1L)).thenReturn(false);

        String viewName = reservationController.deleteReservation(1L, model, redirectAttributes);

        verify(redirectAttributes).addFlashAttribute("errorMessage", "Reservation Couldn't be Cancelled");
        assertEquals("redirect:/ev-charging/delete-form", viewName);
    }
    
    
   
  
    @Test
    void testAddReservationWithoutEVVehicle() throws Exception {
        when(session.getAttribute(ReservationController.USER_ID)).thenReturn(1L);
        when(vehicleRepository.existsElectricVehicleByUserId(1L)).thenReturn(false);
        String viewName = reservationController.addReservationForm(session,model,redirectAttributes);

        assertEquals("redirect:/vehicles/add",viewName);
        verify(redirectAttributes).addFlashAttribute("errorMessage","You currently Don't have an EV to Reserve a spot");
    }
   
    @Test
    void testListReservations_NoReservations() {
        when(session.getAttribute("userId")).thenReturn(USER_ID);
        when(reservationService.getReservationsByUserId(USER_ID)).thenReturn(List.of());

        String viewName = reservationController.listReservations(session, model);

        verify(model).addAttribute("reservations", List.of());
        assertEquals("ViewReservations", viewName);
    }
    
    @Test
    void testListReservations_WhenUserLoggedIn_ShouldReturnViewReservations() {
        Long userId = 1L;
        List<Reservation> mockReservations = List.of(new Reservation());

        when(session.getAttribute("userId")).thenReturn(userId);
        when(reservationService.getReservationsByUserId(userId)).thenReturn(mockReservations);

        String viewName = reservationController.listReservations(session, model);

        verify(model).addAttribute("reservations", mockReservations);
        assertEquals("ViewReservations", viewName);
    }
    @Test
    void testListReservations_WhenUserNotLoggedIn_ShouldRedirectToLogin() {
        when(session.getAttribute("userId")).thenReturn(null);

        String viewName = reservationController.listReservations(session, model);

        assertEquals("redirect:/login", viewName);
    }
    @Test
    void testAddReservationForm_WhenEVSpotAvailable_ShouldReturnAddReservationView() {
        Long userId = 1L;
        when(session.getAttribute("userId")).thenReturn(userId);
        when(vehicleRepository.existsElectricVehicleByUserId(userId)).thenReturn(true);
        when(parkingSpotRepository.findBySpotType("EV_SPOT")).thenReturn(List.of(new ParkingSpot()));
        when(vehicleRepository.findEVVehicles(userId)).thenReturn(List.of("EV123"));

        String viewName = reservationController.addReservationForm(session, model, redirectAttributes);

        verify(model).addAttribute(eq("evSpots"), any());
        verify(model).addAttribute(eq("evVehicles"), any());
        assertEquals("addReservation", viewName);
    }
    @Test
    void testAddReservationForm_WhenNoEVSpots_ShouldRedirectToVehicleAdd() {
        Long userId = 1L;
        when(session.getAttribute("userId")).thenReturn(userId);
        when(vehicleRepository.existsElectricVehicleByUserId(userId)).thenReturn(false);

        String viewName = reservationController.addReservationForm(session, model, redirectAttributes);

        verify(redirectAttributes).addFlashAttribute("errorMessage", "You currently Don't have an EV to Reserve a spot");
        assertEquals("redirect:/vehicles/add", viewName);
    }
    @Test
    void testAddReservation_InvalidDate_ShouldRedirectToAddReservationWithError() throws Exception {
        Long userId = 1L;
        when(session.getAttribute("userId")).thenReturn(userId);

        Reservation reservation = new Reservation();
        reservation.setReservationTime(new Date(System.currentTimeMillis() - 1000)); // Past date

        String result = reservationController.addReservation("EV123", "1", session, reservation, redirectAttributes);

        verify(redirectAttributes).addFlashAttribute("errorMessage", "Please Choose Correct Date and time");
        assertEquals("redirect:/ev-charging/add", result);
    }
    @Test
    void testDeleteReservation_SuccessfulDeletion_ShouldRedirectWithSuccessMessage() {
        Long reservationId = 1L;
        when(reservationService.deleteReservationById(reservationId)).thenReturn(true);

        String result = reservationController.deleteReservation(reservationId, model, redirectAttributes);

        verify(redirectAttributes).addFlashAttribute("successMessage", "Reservation Successfully Cancelled");
        assertEquals("redirect:/ev-charging/delete-form", result);
    }
    @Test
    void testDeleteReservation_UnsuccessfulDeletion_ShouldRedirectWithErrorMessage() {
        Long reservationId = 1L;
        when(reservationService.deleteReservationById(reservationId)).thenReturn(false);

        String result = reservationController.deleteReservation(reservationId, model, redirectAttributes);

        verify(redirectAttributes).addFlashAttribute("errorMessage", "Reservation Couldn't be Cancelled");
        assertEquals("redirect:/ev-charging/delete-form", result);
    }
    @Test
    void testListReservations_UserLoggedIn() {
        when(session.getAttribute(ReservationController.USER_ID)).thenReturn(1L);
        List<Reservation> reservations = new ArrayList<>();
        when(reservationService.getReservationsByUserId(1L)).thenReturn(reservations);

        String result = reservationController.listReservations(session, model);
        assertEquals("ViewReservations", result);
        verify(model).addAttribute(ReservationController.RESERVATIONS, reservations);
    }
    @Test
    void testAddReservation_InvalidInput() {
        when(session.getAttribute(ReservationController.USER_ID)).thenReturn(1L);
        String result = reservationController.addReservation("", "", session, new Reservation(), redirectAttributes);
        assertEquals("redirect:/ev-charging/add", result);
        verify(redirectAttributes).addFlashAttribute(ReservationController.ERROR_MESSAGE, "Invalid vehicle or spot selection.");
    }
    @Test
    void testAddReservation_InvalidReservationTime() {
        when(session.getAttribute(ReservationController.USER_ID)).thenReturn(1L);
        Reservation reservation = new Reservation();
        reservation.setReservationTime(new Date(System.currentTimeMillis() - 10000)); // Past date

        String result = reservationController.addReservation("vehicle1", "spot1", session, reservation, redirectAttributes);
        assertEquals("redirect:/ev-charging/add", result);
        verify(redirectAttributes).addFlashAttribute(ReservationController.ERROR_MESSAGE, "Please Choose Correct Date and time");
    }
    @Test
    void testDeleteReservation_Successful() {
        when(reservationService.deleteReservationById(1L)).thenReturn(true);

        String result = reservationController.deleteReservation(1L, model, redirectAttributes);
        assertEquals("redirect:/ev-charging/delete-form", result);
        verify(redirectAttributes).addFlashAttribute(ReservationController.SUCCESS_MESSAGE, "Reservation Successfully Cancelled");
    }

    @Test
    void testListReservations_UserNotLoggedIn() {
        when(session.getAttribute(ReservationController.USER_ID)).thenReturn(null);
        String result = reservationController.listReservations(session, model);
        assertEquals("redirect:/login", result);
    }
   
  
    @Test
    void testAddReservation_InvalidDate() {
        // Arrange
        Long loggedInUserId = 1L;
        String evVehicle = "EV123";
        String evSpot = "1";
        Reservation reservation = new Reservation();
        reservation.setReservationTime(new java.util.Date(System.currentTimeMillis() - 1000)); // Invalid past date
        when(session.getAttribute("userId")).thenReturn(loggedInUserId);
        
        // Act
        String viewName = reservationController.addReservation(evVehicle, evSpot, session, reservation, redirectAttributes);

        // Assert
        assertEquals("redirect:/ev-charging/add", viewName);
        verify(redirectAttributes, times(1)).addFlashAttribute("errorMessage", "Please Choose Correct Date and time");
    }
    @Test
    void testListReservations_LoggedInUser() {
        // Arrange
        Long loggedInUserId = 1L;
        when(session.getAttribute("userId")).thenReturn(loggedInUserId);
        when(reservationService.getReservationsByUserId(loggedInUserId)).thenReturn(Collections.emptyList());

        // Act
        String viewName = reservationController.listReservations(session, model);

        // Assert
        assertEquals("ViewReservations", viewName);
        verify(reservationService, times(1)).getReservationsByUserId(loggedInUserId);
        verify(model, times(1)).addAttribute("reservations", Collections.emptyList());
    }
    @Test
    void testListReservations_NotLoggedIn() {
        // Arrange
        when(session.getAttribute("userId")).thenReturn(null);

        // Act
        String viewName = reservationController.listReservations(session, model);

        // Assert
        assertEquals("redirect:/login", viewName);
    }

   
    
    @Test
    void testAddReservationForm_NoEV() {
        // Arrange
        Long loggedInUserId = 1L;
        when(session.getAttribute("userId")).thenReturn(loggedInUserId);
        when(vehicleRepository.existsElectricVehicleByUserId(loggedInUserId)).thenReturn(false);

        // Act
        String viewName = reservationController.addReservationForm(session, model, redirectAttributes);

        // Assert
        assertEquals("redirect:/vehicles/add", viewName);
        verify(redirectAttributes, times(1)).addFlashAttribute("errorMessage", "You currently Don't have an EV to Reserve a spot");
    }

 
	private Object emptyList() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	

	

	
	
	@Test
	public void testRedirectWhenUserNotLoggedIn() throws Exception {
	    when(session.getAttribute(ReservationController.USER_ID)).thenReturn(null);

	    mockMvc.perform(get("/ev-charging/list"))
	            .andExpect(status().is3xxRedirection());
	           // .andExpect(redirectedUrl(ReservationController.REDIRECT_TO_LOGIN));
	}
	
	@Test
    void testListReservations_NoUserInSession_ShouldRedirectToLogin() {
        when(session.getAttribute("userId")).thenReturn(null);
        String result = reservationController.listReservations(session, model);
        assertEquals("redirect:/login", result);
    }
	
	 @Test
	    void testListReservations_UserInSession_ShouldReturnViewReservations() {
	        when(session.getAttribute("userId")).thenReturn(USER_ID);
	        List<Reservation> mockReservations = new ArrayList<>();
	        when(reservationService.getReservationsByUserId(USER_ID)).thenReturn(mockReservations);

	        String result = reservationController.listReservations(session, model);

	        verify(model).addAttribute("reservations", mockReservations);
	        assertEquals("ViewReservations", result);
	    }

	    @Test
	    void testAddReservationForm_NoUserInSession_ShouldRedirectToLogin() {
	        when(session.getAttribute("userId")).thenReturn(null);
	        String result = reservationController.addReservationForm(session, model, redirectAttributes);
	        assertEquals("redirect:/login", result);
	    }

	    @Test
	    void testAddReservationForm_UserHasNoEV_ShouldRedirectToAddVehicle() {
	        when(session.getAttribute("userId")).thenReturn(USER_ID);
	        when(vehicleRepository.existsElectricVehicleByUserId(USER_ID)).thenReturn(false);

	        String result = reservationController.addReservationForm(session, model, redirectAttributes);

	        verify(redirectAttributes).addFlashAttribute("errorMessage", "You currently Don't have an EV to Reserve a spot");
	        assertEquals("redirect:/vehicles/add", result);
	    }

	    @Test
	    void testAddReservationForm_UserHasEV_ShouldReturnAddReservationView() {
	        when(session.getAttribute("userId")).thenReturn(USER_ID);
	        when(vehicleRepository.existsElectricVehicleByUserId(USER_ID)).thenReturn(true);

	        List<ParkingSpot> mockEvSpots = Arrays.asList(new ParkingSpot());
	        List<String> mockEvVehicles = Arrays.asList("MH12EV001");
	        when(parkingSpotRepository.findBySpotType("EV_SPOT")).thenReturn(mockEvSpots);
	        when(vehicleRepository.findEVVehicles(USER_ID)).thenReturn(mockEvVehicles);

	        String result = reservationController.addReservationForm(session, model, redirectAttributes);

	        verify(model).addAttribute("evSpots", mockEvSpots);
	        verify(model).addAttribute("evVehicles", mockEvVehicles);
	        assertEquals("addReservation", result);
	    }

	    @Test
	    void testDeleteReservation_NoUserInSession_ShouldRedirectToLogin() {
	        when(session.getAttribute("userId")).thenReturn(null);
	        String result = reservationController.showDeleteForm(session, model);
	        assertEquals("redirect:/login", result);
	    }

	    @Test
	    void testShowDeleteForm_UserInSession_ShouldReturnCancelReservationView() {
	        when(session.getAttribute("userId")).thenReturn(USER_ID);
	        List<Reservation> mockReservations = Arrays.asList(new Reservation());
	        when(reservationService.getReservationsByUserId(USER_ID)).thenReturn(mockReservations);

	        String result = reservationController.showDeleteForm(session, model);

	        verify(model).addAttribute("reservations", mockReservations);
	        assertEquals("CancelReservation", result);
	    }
	
	
	    @Test
	    void testListReservations_WhenSessionIsNull_ShouldRedirectToLogin() {
	        when(session.getAttribute("userId")).thenReturn(null);

	        String result = reservationController.listReservations(session, model);

	        assertEquals("redirect:/login", result);
	    }

	    @Test
	    void testListReservations_WhenSessionHasUser_ShouldReturnViewReservations() {
	        when(session.getAttribute("userId")).thenReturn(USER_ID);
	        List<Reservation> reservations = new ArrayList<>();
	        when(reservationService.getReservationsByUserId(USER_ID)).thenReturn(reservations);

	        String result = reservationController.listReservations(session, model);

	        verify(model).addAttribute("reservations", reservations);
	        assertEquals("ViewReservations", result);
	    }

	    @Test
	    void testAddReservationForm_WhenSessionIsNull_ShouldRedirectToLogin() {
	        when(session.getAttribute("userId")).thenReturn(null);

	        String result = reservationController.addReservationForm(session, model, redirectAttributes);

	        assertEquals("redirect:/login", result);
	    }

	    @Test
	    void testAddReservationForm_WhenUserHasNoEV_ShouldRedirectToAddVehicle() {
	        when(session.getAttribute("userId")).thenReturn(USER_ID);
	        when(vehicleRepository.existsElectricVehicleByUserId(USER_ID)).thenReturn(false);

	        String result = reservationController.addReservationForm(session, model, redirectAttributes);

	        verify(redirectAttributes).addFlashAttribute("errorMessage", "You currently Don't have an EV to Reserve a spot");
	        assertEquals("redirect:/vehicles/add", result);
	    }

	    @Test
	    void testAddReservationForm_WhenEVSpotsAreAvailable_ShouldReturnAddReservationView() {
	        when(session.getAttribute("userId")).thenReturn(USER_ID);
	        when(vehicleRepository.existsElectricVehicleByUserId(USER_ID)).thenReturn(true);
	        List<ParkingSpot> evSpots = List.of(new ParkingSpot());
	        when(parkingSpotRepository.findBySpotType("EV_SPOT")).thenReturn(evSpots);
	        List<String> evVehicles = List.of(EV_VEHICLE);
	        when(vehicleRepository.findEVVehicles(USER_ID)).thenReturn(evVehicles);

	        String result = reservationController.addReservationForm(session, model, redirectAttributes);

	        verify(model).addAttribute("evSpots", evSpots);
	        verify(model).addAttribute("evVehicles", evVehicles);
	        assertEquals("addReservation", result);
	    }

	    @Test
	    void testUpdateSpotDetails_WhenSessionIsNull_ShouldRedirectToLogin() {
	        String result = reservationController.updateSpotDetails(1L, "2024-12-01T10:00", EV_VEHICLE, redirectAttributes, session, model);

	        assertEquals("redirect:/login", result);
	    }

	    @Test
	    void testUpdateSpotDetails_WhenInvalidDateFormat_ShouldRedirectWithErrorMessage() {
	        when(session.getAttribute("userId")).thenReturn(USER_ID);

	        String result = reservationController.updateSpotDetails(1L, "invalid-date", EV_VEHICLE, redirectAttributes, session, model);

	        verify(redirectAttributes).addFlashAttribute("errorMessage", "Exception Invalid date format. Please use the correct format.");
	        assertEquals("redirect:/ev-charging/edit", result);
	    }

	    @Test
	    void testDeleteReservation_WhenSessionIsNull_ShouldRedirectToLogin() {
	        String result = reservationController.showDeleteForm(session, model);

	        assertEquals("redirect:/login", result);
	    }

	    @Test
	    void testDeleteReservation_WhenSessionHasUser_ShouldReturnCancelReservationView() {
	        when(session.getAttribute("userId")).thenReturn(USER_ID);
	        List<Reservation> reservations = List.of(new Reservation());
	        when(reservationService.getReservationsByUserId(USER_ID)).thenReturn(reservations);

	        String result = reservationController.showDeleteForm(session, model);

	        verify(model).addAttribute("reservations", reservations);
	        assertEquals("CancelReservation", result);
	    }
	    
	    @Test
	    void testListReservationsWhenUserIsLoggedIn() {
	        when(session.getAttribute("userId")).thenReturn(MOCK_USER_ID);
	        List<Reservation> mockReservations = new ArrayList<>();
	        when(reservationService.getReservationsByUserId(MOCK_USER_ID)).thenReturn(mockReservations);

	        String viewName = reservationController.listReservations(session, model);

	        assertEquals("ViewReservations", viewName);
	        verify(model).addAttribute("reservations", mockReservations);
	    }

	    @Test
	    void testListReservationsWhenUserNotLoggedIn() {
	        when(session.getAttribute("userId")).thenReturn(null);

	        String viewName = reservationController.listReservations(session, model);

	        assertEquals("redirect:/login", viewName);
	    }

	    @Test
	    void testAddReservationFormWhenUserLoggedInAndHasEV() {
	        when(session.getAttribute("userId")).thenReturn(MOCK_USER_ID);
	        when(vehicleRepository.existsElectricVehicleByUserId(MOCK_USER_ID)).thenReturn(true);

	        List<String> mockEVVehicles = List.of("EV123");
	        when(vehicleRepository.findEVVehicles(MOCK_USER_ID)).thenReturn(mockEVVehicles);

	        String viewName = reservationController.addReservationForm(session, model, redirectAttributes);

	        assertEquals("addReservation", viewName);
	        verify(model).addAttribute("evVehicles", mockEVVehicles);
	    }

	    @Test
	    void testAddReservationFormWhenUserNotLoggedIn() {
	        when(session.getAttribute("userId")).thenReturn(null);

	        String viewName = reservationController.addReservationForm(session, model, redirectAttributes);

	        assertEquals("redirect:/login", viewName);
	    }

	    @Test
	    void testAddReservationFormWhenUserHasNoEV() {
	        when(session.getAttribute("userId")).thenReturn(MOCK_USER_ID);
	        when(vehicleRepository.existsElectricVehicleByUserId(MOCK_USER_ID)).thenReturn(false);

	        String viewName = reservationController.addReservationForm(session, model, redirectAttributes);

	        assertEquals("redirect:/vehicles/add", viewName);
	        verify(redirectAttributes).addFlashAttribute("errorMessage", "You currently Don't have an EV to Reserve a spot");
	    }

	    @Test
	    void testDeleteReservationWhenUserIsLoggedIn() {
	        when(session.getAttribute("userId")).thenReturn(MOCK_USER_ID);
	        when(reservationService.deleteReservationById(1L)).thenReturn(true);

	        String viewName = reservationController.deleteReservation(1L, model, redirectAttributes);

	        assertEquals("redirect:/ev-charging/delete-form", viewName);
	        verify(redirectAttributes).addFlashAttribute("successMessage", "Reservation Successfully Cancelled");
	    }

	    @Test
	    void testDeleteReservationWhenUserNotLoggedIn() {
	        when(session.getAttribute("userId")).thenReturn(null);

	        String viewName = reservationController.showDeleteForm(session, model);

	        assertEquals("redirect:/login", viewName);
	    }

	    @Test
	    void testListReservations_UserNotLoggedIn2() {
	        when(session.getAttribute(ReservationController.USER_ID)).thenReturn(null);
	        String result = reservationController.listReservations(session, model);
	        assertEquals("redirect:/login", result);
	    }

	    @Test
	    void testListReservations_UserLoggedIn1() {
	        Long userId = 1L;
	        when(session.getAttribute(ReservationController.USER_ID)).thenReturn(userId);
	        List<Reservation> mockReservations = new ArrayList<>();
	        when(reservationService.getReservationsByUserId(userId)).thenReturn(mockReservations);

	        String result = reservationController.listReservations(session, model);

	        assertEquals("ViewReservations", result);
	        verify(model).addAttribute(ReservationController.RESERVATIONS, mockReservations);
	    }

	    @Test
	    void testAddReservationForm_UserNotLoggedIn() {
	        when(session.getAttribute(ReservationController.USER_ID)).thenReturn(null);

	        String result = reservationController.addReservationForm(session, model, redirectAttributes);

	        assertEquals("redirect:/login", result);
	    }

	    @Test
	    void testAddReservationForm_NoEVAvailable() {
	        Long userId = 1L;
	        when(session.getAttribute(ReservationController.USER_ID)).thenReturn(userId);
	        when(vehicleRepository.existsElectricVehicleByUserId(userId)).thenReturn(false);

	        String result = reservationController.addReservationForm(session, model, redirectAttributes);

	        assertEquals("redirect:/vehicles/add", result);
	        verify(redirectAttributes).addFlashAttribute(ReservationController.ERROR_MESSAGE, "You currently Don't have an EV to Reserve a spot");
	    }

	    @Test
	    void testAddReservation_InvalidSession() {
	        when(session.getAttribute(ReservationController.USER_ID)).thenReturn(null);
	        Reservation reservation = new Reservation();

	        String result = reservationController.addReservation("EV123", "1", session, reservation, redirectAttributes);

	        assertEquals("redirect:/login", result);
	    }

	    @Test
	    void testAddReservation_InvalidVehicleOrSpotSelection() {
	        Long userId = 1L;
	        when(session.getAttribute(ReservationController.USER_ID)).thenReturn(userId);
	        Reservation reservation = new Reservation();

	        String result = reservationController.addReservation("", "1", session, reservation, redirectAttributes);

	        assertEquals("redirect:/ev-charging/add", result);
	        verify(redirectAttributes).addFlashAttribute(ReservationController.ERROR_MESSAGE, "Invalid vehicle or spot selection.");
	    }

	    @Test
	    void testUpdateReservation_InvalidDate1() throws Exception {
	        Long userId = 1L;
	        when(session.getAttribute(ReservationController.USER_ID)).thenReturn(userId);
	        String invalidDate = "2023-01-01T00:00";
	        when(redirectAttributes.addFlashAttribute(anyString(), anyString())).thenReturn(redirectAttributes);

	        String result = reservationController.updateSpotDetails(1L, invalidDate, "EV123", redirectAttributes, session, model);

	        assertEquals("redirect:/ev-charging/edit", result);
	        verify(redirectAttributes).addFlashAttribute(ReservationController.ERROR_MESSAGE, "Invalid date format. Please use the correct format.");
	    }

	    @Test
	    void testDeleteReservation_UserNotLoggedIn() {
	        when(session.getAttribute(ReservationController.USER_ID)).thenReturn(null);

	        String result = reservationController.showDeleteForm(session, model);

	        assertEquals("redirect:/login", result);
	    }

	  
	   
	    @Test
	    void testAddReservationWithInvalidUser() {
	        when(session.getAttribute(ReservationController.USER_ID)).thenReturn(null);
	        Reservation reservation = new Reservation();

	        String result = reservationController.addReservation("EV123", "1", session, reservation, redirectAttributes);

	        assertEquals("redirect:/login", result);
	    }

	    @Test
	    void testAddReservationWithNullVehicleId() {
	        Long userId = 1L;
	        when(session.getAttribute(ReservationController.USER_ID)).thenReturn(userId);
	        Reservation reservation = new Reservation();

	        String result = reservationController.addReservation(null, "1", session, reservation, redirectAttributes);

	        assertEquals("redirect:/ev-charging/add", result);
	        verify(redirectAttributes).addFlashAttribute(ReservationController.ERROR_MESSAGE, "Invalid vehicle or spot selection.");
	    }

	    @Test
	    void testAddReservationWithMissingParkingSpot() {
	        Long userId = 1L;
	        when(session.getAttribute(ReservationController.USER_ID)).thenReturn(userId);
	        Reservation reservation = new Reservation();

	        String result = reservationController.addReservation("EV123", "", session, reservation, redirectAttributes);

	        assertEquals("redirect:/ev-charging/add", result);
	        verify(redirectAttributes).addFlashAttribute(ReservationController.ERROR_MESSAGE, "Invalid vehicle or spot selection.");
	    }

	   

	    @Test
	    void testListReservations_WithEmptyReservationList() {
	        Long userId = 1L;
	        when(session.getAttribute(ReservationController.USER_ID)).thenReturn(userId);
	        when(reservationService.getReservationsByUserId(userId)).thenReturn(Collections.emptyList());

	        String result = reservationController.listReservations(session, model);

	        assertEquals("ViewReservations", result);
	        verify(model).addAttribute(ReservationController.RESERVATIONS, Collections.emptyList());
	    }

	    
	   

	    @Test
	    void testDeleteReservation_ReservationIdNotFound() {
	        Long reservationId = 999L;
	        when(reservationService.deleteReservationById(reservationId)).thenReturn(false);

	        String result = reservationController.deleteReservation(reservationId, model, redirectAttributes);

	        assertEquals("redirect:/ev-charging/delete-form", result);
	        verify(redirectAttributes).addFlashAttribute(ReservationController.ERROR_MESSAGE, "Reservation Couldn't be Cancelled");
	    }
	    

	    @Test
	    void testListReservations_WithMultipleReservations() {
	        Long userId = 1L;
	        when(session.getAttribute(ReservationController.USER_ID)).thenReturn(userId);

	        Reservation reservation1 = new Reservation();
	        reservation1.setId(1L);
	        Reservation reservation2 = new Reservation();
	        reservation2.setId(2L);

	        when(reservationService.getReservationsByUserId(userId)).thenReturn(Arrays.asList(reservation1, reservation2));

	        String result = reservationController.listReservations(session, model);

	        assertEquals("ViewReservations", result);
	        verify(model).addAttribute(ReservationController.RESERVATIONS, Arrays.asList(reservation1, reservation2));
	    }

	    @Test
	    void testCancelReservation_InvalidReservationId() {
	        Long reservationId = 999L;
	        when(reservationService.deleteReservationById(reservationId)).thenReturn(false);

	        String result = reservationController.deleteReservation(reservationId, model, redirectAttributes);

	        assertEquals("redirect:/ev-charging/delete-form", result);
	        verify(redirectAttributes).addFlashAttribute(ReservationController.ERROR_MESSAGE, "Reservation Couldn't be Cancelled");
	    }
       
	    
	 

	    @Test
	    void testCancelReservation_NonExistentReservation() {
	        Long reservationId = 999L;

	        when(reservationService.deleteReservationById(reservationId)).thenReturn(false);

	        String result = reservationController.deleteReservation(reservationId, model, redirectAttributes);

	        assertEquals("redirect:/ev-charging/delete-form", result);
	        verify(redirectAttributes).addFlashAttribute(ReservationController.ERROR_MESSAGE, "Reservation Couldn't be Cancelled");
	    }

	   

	   

	    @Test
	    void testListReservations_NoReservations1() {
	        Long userId = 1L;
	        when(session.getAttribute(ReservationController.USER_ID)).thenReturn(userId);
	        when(reservationService.getReservationsByUserId(userId)).thenReturn(Arrays.asList());

	        String result = reservationController.listReservations(session, model);

	        assertEquals("ViewReservations", result);
	        verify(model).addAttribute(ReservationController.RESERVATIONS, Arrays.asList());
	    }

	    @Test
	    void testAddReservation_InvalidUserRedirectsToLogin() {
	        when(session.getAttribute(ReservationController.USER_ID)).thenReturn(null);

	        String result = reservationController.addReservation("EV123", "P101", session, new Reservation(), redirectAttributes);

	        assertEquals("redirect:/login", result);
	    }

	    @Test
	    void testReservationNotFoundForDeletion() {
	        Long reservationId = 123L;
	        when(reservationService.deleteReservationById(reservationId)).thenReturn(false);

	        String result = reservationController.deleteReservation(reservationId, model, redirectAttributes);

	        assertEquals("redirect:/ev-charging/delete-form", result);
	        verify(redirectAttributes).addFlashAttribute(ReservationController.ERROR_MESSAGE, "Reservation Couldn't be Cancelled");
	    }
	    
	   
	   

	    

	    @Test
	    void testDeleteReservation_Success1() {
	        Long reservationId = 10L;

	        when(reservationService.deleteReservationById(reservationId)).thenReturn(true);

	        String result = reservationController.deleteReservation(reservationId, model, redirectAttributes);

	        assertEquals("redirect:/ev-charging/delete-form", result);
	        verify(redirectAttributes).addFlashAttribute(ReservationController.SUCCESS_MESSAGE, "Reservation Successfully Cancelled");
	    }

	   

	    @Test
	    void testAddReservation_UserNotLoggedIn() {
	        when(session.getAttribute(ReservationController.USER_ID)).thenReturn(null);

	        String result = reservationController.addReservation("EV123", "PS101", session, new Reservation(), redirectAttributes);

	        assertEquals("redirect:/login", result);
	        verify(redirectAttributes, never()).addFlashAttribute(anyString(), any());
	    }

	  

	    @Test
	    void testReservationCancellation_Failure_DueToInvalidId() {
	        Long invalidReservationId = 999L;
	        when(reservationService.deleteReservationById(invalidReservationId)).thenReturn(false);

	        String result = reservationController.deleteReservation(invalidReservationId, model, redirectAttributes);

	        assertEquals("redirect:/ev-charging/delete-form", result);
	        verify(redirectAttributes).addFlashAttribute(ReservationController.ERROR_MESSAGE, "Reservation Couldn't be Cancelled");
	    }

	    @Test
	    void testListReservations_EmptyReservationList() {
	        Long userId = 1L;
	        when(session.getAttribute(ReservationController.USER_ID)).thenReturn(userId);
	        when(reservationService.getReservationsByUserId(userId)).thenReturn(List.of());

	        String result = reservationController.listReservations(session, model);

	        assertEquals("ViewReservations", result);
	        verify(model).addAttribute(ReservationController.RESERVATIONS, List.of());
	    }

	    @Test
	    void testReservationList_EmptyReservationList() {
	        Long userId = 1L;

	        when(session.getAttribute(ReservationController.USER_ID)).thenReturn(userId);
	        when(reservationService.getReservationsByUserId(userId)).thenReturn(null);

	        String result = reservationController.listReservations(session, model);

	        assertEquals("ViewReservations", result);
	        verify(model).addAttribute(ReservationController.RESERVATIONS, null);
	    }
}

