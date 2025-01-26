package com.quick_park_assist.controllerTest;

import com.quick_park_assist.controller.ReservationController;
import com.quick_park_assist.entity.Reservation;
import com.quick_park_assist.service.IReservationService;
import com.quick_park_assist.repository.VehicleRepository;
import com.quick_park_assist.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.web.servlet.MockMvc;

import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.text.SimpleDateFormat;

import java.util.Collections;
import java.util.Date;

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

    @InjectMocks
    private ReservationController reservationController;
    
    @Test
    void testAddReservation_InvalidVehicleNumber() throws Exception {
        Long userId = 1L;
        Reservation reservation = new Reservation();
        reservation.setReservationTime(new Date()); // Assume valid time
        reservation.setVehicleNumber("InvalidEV123");

        given(vehicleRepository.existsVehicleByVehicleNumberAndUserIdAndEvTrue("InvalidEV123", userId)).willReturn(false);

        mockMvc.perform(post("/ev-charging/add-reservation")
                .sessionAttr("userId", userId)
                .flashAttr("reservation", reservation))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ev-charging/add"));
                
    }
    @Test
    void testAddReservation_PastTime() throws Exception {
        Long userId = 1L;
        Reservation reservation = new Reservation();
        reservation.setReservationTime(new Date(System.currentTimeMillis() - 10000)); // Past time

        mockMvc.perform(post("/ev-charging/add-reservation")
                .sessionAttr("userId", userId)
                .flashAttr("reservation", reservation))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ev-charging/add"))
                .andExpect(flash().attribute("errorMessage", "Please Choose Correct Date and time"));
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
    void testAddReservation_ReservationTimeInThePast() throws Exception {
        Long userId = 1L;
        Reservation reservation = new Reservation();
        reservation.setReservationTime(new Date(System.currentTimeMillis() - 100000)); // Past time
        reservation.setVehicleNumber("EV123");

        given(vehicleRepository.existsVehicleByVehicleNumberAndUserIdAndEvTrue("EV123", userId)).willReturn(true);

        mockMvc.perform(post("/ev-charging/add-reservation")
                .sessionAttr("userId", userId)
                .flashAttr("reservation", reservation))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ev-charging/add"))
                .andExpect(flash().attribute("errorMessage", "Please Choose Correct Date and time"));
    }
    
    @Test
    void testAddReservation_UserNotLoggedIn() throws Exception {
        Reservation reservation = new Reservation();
        reservation.setReservationTime(new Date()); // Assume valid time
        reservation.setVehicleNumber("EV123");

        mockMvc.perform(post("/ev-charging/add-reservation")
                .flashAttr("reservation", reservation))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    
    @Test
    void testAddReservation_VehicleNotEV() throws Exception {
        Long userId = 1L;
        Reservation reservation = new Reservation();
        reservation.setReservationTime(new Date()); // Assume valid time
        reservation.setVehicleNumber("NON_EV123");

        given(vehicleRepository.existsVehicleByVehicleNumberAndUserIdAndEvTrue("NON_EV123", userId)).willReturn(false);

        mockMvc.perform(post("/ev-charging/add-reservation")
                .sessionAttr("userId", userId)
                .flashAttr("reservation", reservation))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ev-charging/add"));
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
    void testAddReservation_ExistingReservationTime() throws Exception {
        Long userId = 1L;
        Reservation reservation = new Reservation();
        reservation.setReservationTime(new Date()); // Assume valid time
        reservation.setVehicleNumber("EV123");

        given(vehicleRepository.existsVehicleByVehicleNumberAndUserIdAndEvTrue("EV123", userId)).willReturn(true);
        mockMvc.perform(post("/ev-charging/add-reservation")
                .sessionAttr("userId", userId)
                .flashAttr("reservation", reservation))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ev-charging/add"));
               
    }
  
    @Test
    void testAddReservationForm_NoSession() throws Exception {
        mockMvc.perform(get("/ev-charging/add"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
    @Test
    void testUpdateReservationInvalidDate() throws Exception {
        Long reservationId = 1L;
        String vehicleNumber = "123ABC";
        String invalidStartTime = "2023-01-01T10:00";

        mockMvc.perform(post("/ev-charging/update-reservation")
                .param("id", String.valueOf(reservationId))
                .param("startTime", invalidStartTime)
                .param("vehicleNumber", vehicleNumber))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ev-charging/edit"));
               
    }
    @Test
    void testDeleteReservation_InvalidHttpMethod() throws Exception {
        mockMvc.perform(get("/ev-charging/delete/{id}", 1L))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void testDeleteReservation_Success() throws Exception {
        Long reservationId = 1L;

        // Mock the service to return true for successful deletion
        given(reservationService.deleteReservationById(reservationId)).willReturn(true);

        // Perform the request and verify the response
        mockMvc.perform(post("/ev-charging/delete/{id}", reservationId)
                        .param("id", String.valueOf(reservationId))) // Pass ID as a request parameter
                .andExpect(status().is3xxRedirection()) // Expect a redirect response
                .andExpect(redirectedUrl("/ev-charging/delete-form"));// Verify redirection
    }

    @Test
    void testDeleteReservation_Failure() throws Exception {
        Long reservationId = 1L;

        // Mock the service to return false for failed deletion
        given(reservationService.deleteReservationById(reservationId)).willReturn(false);

        // Perform the request and verify the response
        mockMvc.perform(post("/ev-charging/delete/{id}", reservationId)
                        .param("id", String.valueOf(reservationId))) // Pass ID as a request parameter
                .andExpect(status().is3xxRedirection()) // Expect a redirect response
                .andExpect(redirectedUrl("/ev-charging/delete-form")) // Verify redirection
                .andExpect(flash().attribute("errorMessage", "Reservation Couldn't be Cancelled")); // Verify flash attribute
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
    void testAddReservation_InvalidOwnership() throws Exception {
        Long userId = 1L;
        String startTimeStr = "2023-01-01T10:00"; // Past date

        // Parse startTime from the ISO format
        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

        Date startTime = dateTimeFormatter.parse(startTimeStr);
        Reservation reservation = new Reservation();
        reservation.setReservationTime(startTime); // Assume valid time
        reservation.setVehicleNumber("WrongEV123");

        // Simulate vehicle not belonging to the user
        given(vehicleRepository.existsVehicleByVehicleNumberAndUserIdAndEvTrue("WrongEV123", userId)).willReturn(false);

        mockMvc.perform(post("/ev-charging/add-reservation")
                        .sessionAttr("userId", userId)
                        .flashAttr("reservation", reservation))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ev-charging/add"))
                .andExpect(flash().attribute("errorMessage", "Please Choose Correct Date and time"));
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
    void testAddReservationWithInvalidDate() {
        Reservation reservation = new Reservation();
        reservation.setReservationTime(new Date(System.currentTimeMillis() - 10000));
        when(session.getAttribute("userId")).thenReturn(1L);
        String result = reservationController.addReservation(session, reservation, redirectAttributes);
        assertEquals("redirect:/ev-charging/add", result);
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
    void testAddReservationWithFutureDate() {
        Reservation reservation = new Reservation();
        reservation.setReservationTime(new Date(System.currentTimeMillis() + 86400000));
        when(session.getAttribute("userId")).thenReturn(1L);
        when(vehicleRepository.existsVehicleByVehicleNumberAndUserIdAndEvTrue(any(), eq(1L))).thenReturn(true);
        String result = reservationController.addReservation(session, reservation, redirectAttributes);
        assertEquals("redirect:/ev-charging/add", result);
    }

    @Test
    void testAddReservationWithExistingUser() {
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(new com.quick_park_assist.entity.User()));
        Reservation reservation = new Reservation();
        reservation.setVehicleNumber("ABC123");
        reservation.setReservationTime(new Date(System.currentTimeMillis() + 10000));
        when(vehicleRepository.existsVehicleByVehicleNumberAndUserIdAndEvTrue(any(), eq(1L))).thenReturn(true);
        String result = reservationController.addReservation(session, reservation, redirectAttributes);
        assertEquals("redirect:/ev-charging/add", result);
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
    void testRedirectAfterSuccessfulReservationUpdate() {
        when(reservationService.updateSpotDetails(anyLong(), any(), anyString())).thenReturn(true);
        String result = reservationController.updateSpotDetails(1L, "2025-01-01T10:00", "ABC123", redirectAttributes, model);
        assertEquals("redirect:/ev-charging/edit", result);
    }

    @Test
    void testRedirectAfterFailedReservationUpdate() {
        when(reservationService.updateSpotDetails(anyLong(), any(), anyString())).thenReturn(false);
        String result = reservationController.updateSpotDetails(1L, "2025-01-01T10:00", "ABC123", redirectAttributes, model);
        assertEquals("redirect:/ev-charging/edit", result);
    }

    @Test
    void testReservationAdditionForNullUser() {
        when(session.getAttribute("userId")).thenReturn(null);
        Reservation reservation = new Reservation();
        String result = reservationController.addReservation(session, reservation, redirectAttributes);
        assertEquals("redirect:/login", result);
    }

    @Test
    void testDeleteReservationByInvalidId() {
        when(reservationService.deleteReservationById(2L)).thenReturn(false);
        String result = reservationController.deleteReservation(2L, model, redirectAttributes);
        assertEquals("redirect:/ev-charging/delete-form", result);
    }

}
