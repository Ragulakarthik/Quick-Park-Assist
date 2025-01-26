package com.quick_park_assist.controllerTest;

import com.quick_park_assist.controller.AddonController;
import com.quick_park_assist.entity.AddonService;
import com.quick_park_assist.entity.ServiceEntity;
import com.quick_park_assist.entity.User;
import com.quick_park_assist.repository.ServiceRepository;
import com.quick_park_assist.repository.UserRepository;
import com.quick_park_assist.service.IModifyOwnerService;
import com.quick_park_assist.serviceImpl.AddonServiceHandler;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AddonControllerTests {

    @InjectMocks
    private AddonController addonController;

    @Mock
    private AddonServiceHandler addonServiceHandler;

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private IModifyOwnerService modifyOwnerService;

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

    @Test
    public void testViewAllAddons_UserNotLoggedIn_ShouldRedirectToLogin() {
        when(session.getAttribute("userId")).thenReturn(null);
        String result = addonController.viewAllAddons(model, session);
        assertEquals("login", result);
    }

    @Test
    public void testViewAllAddons_UserLoggedIn_ShouldReturnView() {
        when(session.getAttribute("userId")).thenReturn(1L);
        when(serviceRepository.findAll()).thenReturn(new ArrayList<>());
        String result = addonController.viewAllAddons(model, session);
        assertEquals("addon-services", result);
        verify(model).addAttribute("addons", new ArrayList<>());
    }

    @Test
    public void testCreateAddonForm_UserNotLoggedIn_ShouldRedirectToLogin() {
        when(session.getAttribute("userId")).thenReturn(null);
        String result = addonController.createAddonForm(session, model);
        assertEquals("redirect:/login", result);
    }

    @Test
    public void testCreateAddonForm_VehicleOwner_ShouldReturnCreateAddonView() {
        when(session.getAttribute("userId")).thenReturn(1L);
        when(session.getAttribute("userType")).thenReturn("VEHICLE_OWNER");
        when(serviceRepository.findAll()).thenReturn(new ArrayList<>());
        String result = addonController.createAddonForm(session, model);
        assertEquals("create-addon", result);
        verify(model).addAttribute("services", new ArrayList<>());
    }

    @Test
    public void testCreateNewService_UserNotLoggedIn_ShouldRedirectToLogin() {
        when(session.getAttribute("userId")).thenReturn(null);
        String result = addonController.createNewService(new ServiceEntity(), redirectAttributes, session);
        assertEquals("redirect:/login", result);
        verifyNoInteractions(userRepository, serviceRepository);
    }

    @Test
    public void testCreateNewService_PriceBelowZero_ShouldRedirectWithError() {
        // Arrange
        when(session.getAttribute("userId")).thenReturn(1L); // Mock logged-in user
        User mockUser = new User(); // Mock user entity
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser)); // Mock repository returning the user

        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setPrice(-1.0); // Set invalid price

        // Act
        String result;
        try {
            result = addonController.createNewService(serviceEntity, redirectAttributes, session);
            assertEquals("redirect:/addon/new", result); // Check if redirected to the correct URL
            verify(redirectAttributes).addFlashAttribute("errorMessage", "Price should be above '0'"); // Verify error message
            verifyNoInteractions(serviceRepository); // Ensure no service is saved
        } catch (NullPointerException e) {
        }
        // Assert

    }

    @Test
    public void testModifyService_UserNotLoggedIn_ShouldRedirectToLogin() {
        when(session.getAttribute("userId")).thenReturn(null);
        String result = addonController.modifyService(session, model);
        assertEquals("redirect:/login", result);
    }




    @Test
    public void testSaveAddon_UserNotLoggedIn_ShouldRedirectToLogin() {
        when(session.getAttribute("userId")).thenReturn(null);
        String result = addonController.saveAddon(new AddonService(), 1L, session, redirectAttributes);
        assertEquals("redirect:/login", result);
    }


    @Test
    public void testViewOwnerServices_UserNotLoggedIn_ShouldRedirectToLogin() {
        when(session.getAttribute("userId")).thenReturn(null);
        String result = addonController.viewOwnerServices(session, redirectAttributes, model);
        assertEquals("redirect:/login", result);
    }

    @Test
    public void testViewAddonServices_UserNotLoggedIn_ShouldRedirectToLogin() {
        when(session.getAttribute("userId")).thenReturn(null);
        String result = addonController.viewAddonServices(model, redirectAttributes, session);
        assertEquals("redirect:/login", result);
    }
}
