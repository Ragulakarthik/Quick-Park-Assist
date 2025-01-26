package com.quick_park_assist.controllerTest;

import com.quick_park_assist.controller.VehicleController;
import com.quick_park_assist.dto.VehicleDTO;
import com.quick_park_assist.entity.Vehicle;
import com.quick_park_assist.repository.VehicleRepository;
import com.quick_park_assist.service.IVehicleService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class VehicleControllerTest {

    @Mock
    private IVehicleService vehicleService;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private VehicleController vehicleController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListVehicles_UserNotInSession() {
        when(session.getAttribute("userId")).thenReturn(null);
        String viewName = vehicleController.listVehicles(session, model);
        assertEquals("redirect:/login", viewName);
        verifyNoInteractions(model);
    }

    @Test
    void testListVehicles_UserInSession() {
        Long userId = 1L;
        when(session.getAttribute("userId")).thenReturn(userId);
        List<Vehicle> vehicles = new ArrayList<>();
        vehicles.add(new Vehicle());
        when(vehicleService.getVehiclesByUserId(userId)).thenReturn(vehicles);

        String viewName = vehicleController.listVehicles(session, model);
        assertEquals("EditVehicle", viewName);
        verify(model).addAttribute("vehicles", vehicles);
    }

    @Test
    void testAddVehicle_ValidData() {
        VehicleDTO vehicleDTO = new VehicleDTO();
        when(bindingResult.hasErrors()).thenReturn(false);
        when(session.getAttribute("userId")).thenReturn(1L);
        when(vehicleRepository.existsVehicleByVehicleNumber(anyString())).thenReturn(false);

        String viewName = vehicleController.addVehicle(vehicleDTO, bindingResult, session, redirectAttributes);
        assertEquals("redirect:/dashboard", viewName);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Vehicle added successfully!");
    }

    @Test
    void testAddVehicle_DuplicateVehicleNumber() {
        VehicleDTO vehicleDTO = new VehicleDTO();
        vehicleDTO.setVehicleNumber("ABC123");
        when(bindingResult.hasErrors()).thenReturn(false);
        when(session.getAttribute("userId")).thenReturn(1L);
        when(vehicleRepository.existsVehicleByVehicleNumber("ABC123")).thenReturn(true);

        String viewName = vehicleController.addVehicle(vehicleDTO, bindingResult, session, redirectAttributes);
        assertEquals("redirect:/vehicles/add", viewName);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Vehicle Already Registered. Enter new Vehicle Number!");
    }

    @Test
    void testAddVehicle_InvalidData() {
        VehicleDTO vehicleDTO = new VehicleDTO();
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = vehicleController.addVehicle(vehicleDTO, bindingResult, session, redirectAttributes);
        assertEquals("AddVehicle", viewName);
    }

    @Test
    void testViewVehicle_NotInSession() {
        when(session.getAttribute("userId")).thenReturn(null);
        String viewName = vehicleController.viewVehicle(1L, session, model, redirectAttributes);
        assertEquals("redirect:/login", viewName);
    }

    @Test
    void testViewVehicle_Valid() {
        Long userId = 1L;
        Long vehicleId = 1L;
        Vehicle vehicle = new Vehicle();
        when(session.getAttribute("userId")).thenReturn(userId);
        when(vehicleService.getVehicleByIdAndUserId(vehicleId, userId)).thenReturn(vehicle);

        String viewName = vehicleController.viewVehicle(vehicleId, session, model, redirectAttributes);
        assertEquals("ListVehicle", viewName);
        verify(model).addAttribute("vehicle", vehicle);
    }

    @Test
    void testViewVehicle_NotFound() {
        Long userId = 1L;
        Long vehicleId = 1L;
        when(session.getAttribute("userId")).thenReturn(userId);
        when(vehicleService.getVehicleByIdAndUserId(vehicleId, userId)).thenThrow(new RuntimeException("Vehicle not found"));

        String viewName = vehicleController.viewVehicle(vehicleId, session, model, redirectAttributes);
        assertEquals("redirect:/dashboard", viewName);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Vehicle not found");
    }

    @Test
    void testDeleteVehicle_Valid() {
        Long userId = 1L;
        Long vehicleId = 1L;
        when(session.getAttribute("userId")).thenReturn(userId);

        String viewName = vehicleController.deleteVehicle(vehicleId, session, redirectAttributes);
        assertEquals("redirect:/dashboard", viewName);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Vehicle deleted successfully!");
    }
}

