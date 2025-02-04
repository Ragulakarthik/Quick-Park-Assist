package com.quick_park_assist.controllerTest;

import com.quick_park_assist.controller.AddonController;
import com.quick_park_assist.entity.AddonService;
import com.quick_park_assist.entity.ServiceEntity;
import com.quick_park_assist.entity.User;
import com.quick_park_assist.repository.ServiceRepository;
import com.quick_park_assist.repository.UserRepository;
import com.quick_park_assist.service.IModifyOwnerService;
import com.quick_park_assist.serviceImpl.AddonServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
 class AddonControllerTests {

    @InjectMocks
    private AddonController addonController;

    @Mock
    private AddonServiceImpl addonServiceHandler;

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
        MockitoAnnotations.openMocks(this); // Initializes mocks and injects dependencies
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
    public void testCreateAddonForm_SpotOwner_ShouldReturnCreateServiceView() {
        // Arrange
        when(session.getAttribute("userId")).thenReturn(1L);
        when(session.getAttribute("userType")).thenReturn("SPOT_OWNER");
        
        // Act
        String result = addonController.createAddonForm(session, model);

        // Assert
        assertEquals("CreateService", result);
        
        // Capture the newService argument passed to model.addAttribute
        ArgumentCaptor<ServiceEntity> serviceCaptor = ArgumentCaptor.forClass(ServiceEntity.class);
        verify(model).addAttribute(eq("newService"), serviceCaptor.capture());

        // Assert that the captured ServiceEntity is not null and check its properties if needed
        ServiceEntity capturedService = serviceCaptor.getValue();
        assertNotNull(capturedService); // Ensure it's not null
        // You can add more assertions here to check specific properties of capturedService if needed
    }

    @Test
    public void testCreateNewService_UserNotLoggedIn_ShouldRedirectToLogin() {
        when(session.getAttribute("userId")).thenReturn(null);
        String result = addonController.createNewService(new ServiceEntity(), redirectAttributes, session);
        assertEquals("redirect:/login", result);
    }

    @Test
    public void testCreateNewService_UserNotFound_ShouldThrowException() {
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            addonController.createNewService(new ServiceEntity(), redirectAttributes, session)
        );
        assertEquals("User not found", exception.getMessage());
    }



    @Test
    public void testCreateNewService_ServiceExists_ShouldRedirectWithError() {
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setName("Existing Service");
        serviceEntity.setPrice(10.0);
        when(serviceRepository.existsServiceEntityByNameIgnoreCase("Existing Service")).thenReturn(true);
        String result = addonController.createNewService(serviceEntity, redirectAttributes, session);
        assertEquals("redirect:/addon/new", result);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "The Service is already created By you!");
    }

    @Test
    public void testCreateNewService_SuccessfulCreation_ShouldRedirectWithSuccessMessage() {
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setName("New Service");
        serviceEntity.setPrice(10.0);
        when(serviceRepository.existsServiceEntityByNameIgnoreCase("New Service")).thenReturn(false);

        String result = addonController.createNewService(serviceEntity, redirectAttributes, session);
        assertEquals("redirect:/addon/new", result);
        
        verify(serviceRepository).save(serviceEntity);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Your Service is Successfully created!");
    }


    @Test
    public void testModifyService_UserNotLoggedIn_ShouldRedirectToLogin() {
        // Arrange
        when(session.getAttribute("userId")).thenReturn(null);

        // Act
        String result = addonController.modifyService(session, model);

        // Assert
        assertEquals("redirect:/login", result);
    }

    @Test
    public void testModifyService_NoServices_ShouldAddErrorMessage() {
        // Arrange
        Long userId = 1L;
        when(session.getAttribute("userId")).thenReturn(userId);
        when(modifyOwnerService.getOwnerServices(userId)).thenReturn(new ArrayList<>());

        // Act
        String result = addonController.modifyService(session, model);

        // Assert
        assertEquals("EditOwnerService", result);
        verify(model).addAttribute("errorMessage", "You currently do not have any services");
    }

    @Test
    public void testModifyService_WithServices_ShouldAddServicesToModel() {
        // Arrange
        Long userId = 1L;
        List<ServiceEntity> services = new ArrayList<>();
        ServiceEntity service1 = new ServiceEntity();
        service1.setName("Service 1");
        services.add(service1);

        when(session.getAttribute("userId")).thenReturn(userId);
        when(modifyOwnerService.getOwnerServices(userId)).thenReturn(services);

        // Act
        String result = addonController.modifyService(session, model);

        // Assert
        assertEquals("EditOwnerService", result);
        verify(model).addAttribute("ownerServices", services);
    }

    @Test
    public void testModifyOwnerSerice_SuccessfulUpdate() {
        when(modifyOwnerService.updateServiceDetails(any(), any(), any(), any())).thenReturn(true);
        String result = addonController.modifyOwnerSerice(1L, "Service", "Description", 10.0, redirectAttributes);
        assertEquals("redirect:/addon/modify-service", result);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Booking updated successfully!");
    }

    @Test
    public void testModifyOwnerSerice_UpdateFailed() {
        when(modifyOwnerService.updateServiceDetails(any(), any(), any(), any())).thenReturn(false);
        String result = addonController.modifyOwnerSerice(1L, "Service", "Description", 10.0, redirectAttributes);
        assertEquals("redirect:/addon/modify-service", result);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Update failed. UserID not found.");
    }

    @Test
    public void testSaveAddon_UserNotLoggedIn_ShouldRedirectToLogin() {
        when(session.getAttribute("userId")).thenReturn(null);

        String result = addonController.saveAddon(new AddonService(), 1L, session, redirectAttributes);

        assertEquals("redirect:/login", result);
    }

    @Test
    public void testSaveAddon_UserNotFound_ShouldThrowException() {
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            addonController.saveAddon(new AddonService(), 1L, session, redirectAttributes)
        );
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void testSaveAddon_InvalidDuration_ShouldRedirectWithError() {
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));

        AddonService addonService = new AddonService();
        addonService.setDuration("-1"); // Invalid duration
        addonService.setName("Valid Name"); // Valid name

        String result = addonController.saveAddon(addonService, 1L, session, redirectAttributes);

        assertEquals("redirect:/addon/new", result);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Please Choose correct Service duration");
    }

    @Test
    public void testSaveAddon_EmptyName_ShouldRedirectWithError() {
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));

        AddonService addonService = new AddonService();
        addonService.setDuration("10"); // Valid duration
        addonService.setName("");       // Empty name

        String result = addonController.saveAddon(addonService, 1L, session, redirectAttributes);

        assertEquals("redirect:/addon/new", result);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Please Choose correct Service duration");
    }

    @Test
    public void testSaveAddon_ServiceIdNotFound_ShouldRedirectWithError() {
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(serviceRepository.findById(1L)).thenReturn(Optional.empty());

        AddonService addonService = new AddonService();
        addonService.setDuration("10"); // Valid duration
        addonService.setName("Test Service"); // Valid name

        String result = addonController.saveAddon(addonService, 1L, session, redirectAttributes);

        assertEquals("redirect:/addon/new", result);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Service ID not Found Please Try Again!");
    }

    @Test
    public void testSaveAddon_ValidService_ShouldAddAddonAndRedirect() {
        when(session.getAttribute("userId")).thenReturn(1L);
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ServiceEntity serviceEntity = new ServiceEntity();
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(serviceEntity));

        AddonService addonService = new AddonService();
        addonService.setDuration("10"); // Valid duration
        addonService.setName("Test Service"); // Valid name

        doNothing().when(addonServiceHandler).saveAddon(addonService);

        String result = addonController.saveAddon(addonService, 1L, session, redirectAttributes);

        assertEquals("redirect:/addon/new", result);
        verify(addonServiceHandler, times(1)).saveAddon(addonService);
        verify(redirectAttributes).addFlashAttribute("successMessage", "You Selected Service is now Added");
    }


    @Test
    public void testViewOwnerServices_UserNotLoggedIn_ShouldRedirectToLogin() {
        // Arrange
        when(session.getAttribute("userId")).thenReturn(null);

        // Act
        String result = addonController.viewOwnerServices(session, redirectAttributes, model);

        // Assert
        assertEquals("redirect:/login", result);
    }

    @Test
    public void testViewOwnerServices_NoServices_ShouldAddFlashErrorMessage() {
        // Arrange
        Long userId = 1L;
        when(session.getAttribute("userId")).thenReturn(userId);
        when(modifyOwnerService.getOwnerServices(userId)).thenReturn(new ArrayList<>());

        // Act
        String result = addonController.viewOwnerServices(session, redirectAttributes, model);

        // Assert
        assertEquals("viewOwnerService", result);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "You Currently Have no services to SHOW!");
    }

    @Test
    public void testViewOwnerServices_WithServices_ShouldAddServicesToModel() {
        // Arrange
        Long userId = 1L;
        List<ServiceEntity> services = new ArrayList<>();
        ServiceEntity service1 = new ServiceEntity();
        service1.setName("Service 1");
        services.add(service1);

        when(session.getAttribute("userId")).thenReturn(userId);
        when(modifyOwnerService.getOwnerServices(userId)).thenReturn(services);

        // Act
        String result = addonController.viewOwnerServices(session, redirectAttributes, model);

        // Assert
        assertEquals("viewOwnerService", result);
        verify(model).addAttribute("ownerServices", services);
    }


    @Test
    public void testViewAddonServices_UserNotLoggedIn_ShouldRedirectToLogin() {
        // Arrange
        when(session.getAttribute("userId")).thenReturn(null);

        // Act
        String result = addonController.viewAddonServices(model, redirectAttributes, session);

        // Assert
        assertEquals("redirect:/login", result);
    }

    @Test
    public void testViewAddonServices_NoAddonServices_ShouldAddFlashErrorMessage() {
        // Arrange
        when(session.getAttribute("userId")).thenReturn(1L);
        when(addonServiceHandler.getAllAddons()).thenReturn(new ArrayList<>());

        // Act
        String result = addonController.viewAddonServices(model, redirectAttributes, session);

        // Assert
        assertEquals("view-addon-services", result);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "No Services are Available");
    }

    @Test
    public void testViewAddonServices_AddonServicesAvailable_ShouldAddToModel() {
        // Arrange
        when(session.getAttribute("userId")).thenReturn(1L);
        List<AddonService> addons = new ArrayList<>();
        addons.add(new AddonService()); // Mock AddonService instance
        when(addonServiceHandler.getAllAddons()).thenReturn(addons);

        // Act
        String result = addonController.viewAddonServices(model, redirectAttributes, session);

        // Assert
        assertEquals("view-addon-services", result);
        verify(model).addAttribute("addons", addons);
    }


    @Test
    public void testViewAddonServices_NoAddons_ShouldAddErrorMessage() {
        when(session.getAttribute("userId")).thenReturn(1L);
        when(addonServiceHandler.getAllAddons()).thenReturn(new ArrayList<>());
        String result = addonController.viewAddonServices(model, redirectAttributes, session);
        assertEquals("view-addon-services", result);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "No Services are Available");
    }

    @Test
    public void testModifyAddonDuration_ServiceNotFound_ShouldShowError() {
        when(serviceRepository.findById(any())).thenReturn(Optional.empty());
        String result = addonController.modifyAddonDuration(1L, "2", "1", redirectAttributes);
        assertEquals("redirect:/addon/modify-duration", result);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Couldn't Update the Price try Again!");
    }

    @Test
    public void testModifyAddonDuration_ValidUpdate_ShouldUpdatePrice() {
        ServiceEntity service = new ServiceEntity();
        service.setPrice(10.0);
        when(serviceRepository.findById(any())).thenReturn(Optional.of(service));
        String result = addonController.modifyAddonDuration(1L, "2", "1", redirectAttributes);
        assertEquals("redirect:/addon/modify-duration", result);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Modification is Successful!");
    }

    @Test
    public void testDeleteAddon_SuccessfulDelete() {
        String result = addonController.deleteAddon(1L, redirectAttributes); // No unnecessary stubbing
        assertEquals("redirect:/addon/delete", result);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Service Successfully Removed!");
    }

    @Test
    public void testDeleteAddon_Failure() {
        String result = addonController.deleteAddon(1L, redirectAttributes); // No unnecessary stubbing
        assertEquals("redirect:/addon/delete", result);
    }


    @Test
    public void testRemoveService_Success() {
        when(session.getAttribute("userId")).thenReturn(1L);
        when(modifyOwnerService.removeService(1L, 1L)).thenReturn(true);
        String result = addonController.removeService(1L, model, session, redirectAttributes);
        assertEquals("redirect:/addon/remove-service", result);
        verify(redirectAttributes).addFlashAttribute("successMessage", "The Selected Service is successfully removed!");
    }

    @Test
    public void testRemoveService_Failure() {
        when(session.getAttribute("userId")).thenReturn(1L);
        when(modifyOwnerService.removeService(1L, 1L)).thenReturn(false);
        String result = addonController.removeService(1L, model, session, redirectAttributes);
        assertEquals("redirect:/addon/remove-service", result);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Your Selected Service couldn't be removed!");
    }
    @Test
    public void testEditAddonForm_UserNotLoggedIn_ShouldRedirectToLogin() {
        // Arrange
        when(session.getAttribute("userId")).thenReturn(null);

        // Act
        String result = addonController.editAddonForm(1L, model, session);

        // Assert
        assertEquals("redirect:/login", result);
    }

    @Test
    public void testEditAddonForm_UserLoggedInWithServices_ShouldReturnCreateAddonView() {
        // Arrange
        Long userId = 1L;
        when(session.getAttribute("userId")).thenReturn(userId);

        List<AddonService> addonServices = new ArrayList<>();
        AddonService service1 = new AddonService();
        service1.setName("Service 1");
        AddonService service2 = new AddonService();
        service2.setName("Service 2");
        addonServices.add(service1);
        addonServices.add(service2);

        when(addonServiceHandler.getAddonByUserId(userId)).thenReturn(addonServices);

        // Act
        String result = addonController.editAddonForm(1L, model, session);

        // Assert
        assertEquals("create-addon", result);
        verify(model).addAttribute("addon", addonServices);
    }

    @Test
    public void testEditAddonForm_UserLoggedInWithoutServices_ShouldReturnCreateAddonView() {
        // Arrange
        Long userId = 1L;
        when(session.getAttribute("userId")).thenReturn(userId);

        List<AddonService> addonServices = new ArrayList<>();
        when(addonServiceHandler.getAddonByUserId(userId)).thenReturn(addonServices);

        // Act
        String result = addonController.editAddonForm(1L, model, session);

        // Assert
        assertEquals("create-addon", result);
        verify(model).addAttribute("addon", addonServices);
    }
    @Test
    public void testUpdateAddon_SuccessfulUpdate() {
        // Arrange
        Long addonId = 1L;
        AddonService addonService = new AddonService();
        RedirectAttributes mockRedirectAttributes = mock(RedirectAttributes.class);

        // Act
        String result = addonController.updateAddon(addonId, addonService, mockRedirectAttributes);

        // Assert
        verify(addonServiceHandler, times(1)).updateAddon(addonId, addonService);
        verify(mockRedirectAttributes, times(1))
                .addFlashAttribute("successMessage", "Modification is Successful!");
        assertEquals("redirect:/addon/all", result);
    }
    @Test
    public void testModifyAddonDurationForm_UserNotLoggedIn_ShouldRedirectToLogin() {
        // Arrange
        when(session.getAttribute("userId")).thenReturn(null);

        // Act
        String result = addonController.modifyAddonDurationForm(model, session);

        // Assert
        assertEquals("login", result);
    }

    @Test
    public void testModifyAddonDurationForm_UserLoggedIn_ShouldReturnModifyAddonDurationView() {
        // Arrange
        when(session.getAttribute("userId")).thenReturn(1L);
        List<AddonService> addons = new ArrayList<>();
        AddonService addon1 = new AddonService();
        addon1.setName("Addon 1");
        AddonService addon2 = new AddonService();
        addon2.setName("Addon 2");
        addons.add(addon1);
        addons.add(addon2);
        when(addonServiceHandler.getAllAddons()).thenReturn(addons);

        // Act
        String result = addonController.modifyAddonDurationForm(model, session);

        // Assert
        assertEquals("modify-addon-duration", result);
        verify(addonServiceHandler).getAllAddons();
        verify(model).addAttribute("addons", addons);
    }

    @Test
    public void testDeleteAddonPage_UserNotLoggedIn_ShouldRedirectToLogin() {
        // Arrange
        when(session.getAttribute("userId")).thenReturn(null);

        // Act
        String result = addonController.deleteAddonPage(model, session);

        // Assert
        assertEquals("redirect:/login", result);
    }

    @Test
    public void testDeleteAddonPage_UserLoggedIn_ShouldReturnDeleteServiceView() {
        // Arrange
        Long userId = 1L;
        when(session.getAttribute("userId")).thenReturn(userId);
        
        List<AddonService> addons = new ArrayList<>();
        AddonService addon1 = new AddonService();
        addon1.setName("Test Addon");
        addons.add(addon1);
        
        when(addonServiceHandler.getAllAddons()).thenReturn(addons);

        // Act
        String result = addonController.deleteAddonPage(model, session);

        // Assert
        assertEquals("delete-service", result);
        verify(model).addAttribute("addons", addons);
    }
    @Test
    public void testRemoveService_UserNotLoggedIn_ShouldRedirectToLogin() {
        when(session.getAttribute("userId")).thenReturn(null);

        String result = addonController.removeService(1L, model, session, redirectAttributes);

        assertEquals("redirect:/login", result);
    }

    @Test
    public void testRemoveService_InvalidServiceId_ShouldRedirectWithError() {
        when(session.getAttribute("userId")).thenReturn(1L);

        String result = addonController.removeService(null, model, session, redirectAttributes);

        assertEquals("redirect:/addon/remove-service", result);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Invalid Service ID.");
    }

    @Test
    public void testRemoveService_ServiceRemovedSuccessfully_ShouldRedirectWithSuccess() {
        when(session.getAttribute("userId")).thenReturn(1L);
        when(modifyOwnerService.removeService(1L, 1L)).thenReturn(true);

        String result = addonController.removeService(1L, model, session, redirectAttributes);

        assertEquals("redirect:/addon/remove-service", result);
        verify(redirectAttributes).addFlashAttribute("successMessage", "The Selected Service is successfully removed!");
    }

    @Test
    public void testRemoveService_ServiceRemovalFailed_ShouldRedirectWithError() {
        when(session.getAttribute("userId")).thenReturn(1L);
        when(modifyOwnerService.removeService(1L, 1L)).thenReturn(false);

        String result = addonController.removeService(1L, model, session, redirectAttributes);

        assertEquals("redirect:/addon/remove-service", result);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Your Selected Service couldn't be removed!");
    }
    @Test
    public void testShowRemoveServiceForm_UserNotLoggedIn_ShouldRedirectToLogin() {
        // Arrange
        when(session.getAttribute("userId")).thenReturn(null);

        // Act
        String result = addonController.showRemoveServiceForm(session, model);

        // Assert
        assertEquals("redirect:/login", result);
    }

    @Test
    public void testShowRemoveServiceForm_NoServices_ShouldAddErrorMessage() {
        // Arrange
        Long userId = 1L;
        when(session.getAttribute("userId")).thenReturn(userId);
        when(modifyOwnerService.getOwnerServices(userId)).thenReturn(new ArrayList<>());

        // Act
        String result = addonController.showRemoveServiceForm(session, model);

        // Assert
        assertEquals("RemoveOwnerService", result);
        verify(model).addAttribute("errorMessage", "You currently do not have any services");
    }

    @Test
    public void testShowRemoveServiceForm_WithServices_ShouldReturnView() {
        // Arrange
        Long userId = 1L;
        List<ServiceEntity> services = new ArrayList<>();
        services.add(new ServiceEntity()); // Adding a dummy service
        when(session.getAttribute("userId")).thenReturn(userId);
        when(modifyOwnerService.getOwnerServices(userId)).thenReturn(services);

        // Act
        String result = addonController.showRemoveServiceForm(session, model);

        // Assert
        assertEquals("RemoveOwnerService", result);
        verify(model).addAttribute("ownerServices", services);
    }
    @Test
     void testSaveAddon_ValidInput_ShouldSaveAddonAndRedirect() {
        when(session.getAttribute("userId")).thenReturn(1L); // Mock logged-in user
        User mockUser = new User(); // Mock user entity
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser)); // Mock repository returning the user

        ServiceEntity serviceEntity = new ServiceEntity();
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(serviceEntity));

        AddonService addonService = new AddonService();
        addonService.setDuration("10");
        addonService.setName("Test Service");

        String result = addonController.saveAddon(addonService, 1L, session, redirectAttributes);
        assertEquals("redirect:/addon/new", result);

        verify(addonServiceHandler).saveAddon(addonService);
        verify(redirectAttributes).addFlashAttribute("successMessage", "You Selected Service is now Added");
    }

    @Test
    void testCreateNewService_PriceBelowZero_ShouldRedirectWithError() {
        // Arrange
        when(session.getAttribute("userId")).thenReturn(1L); // Mock logged-in user
        User mockUser = new User(); // Mock user entity
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser)); // Mock repository returning the user

        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setPrice(-1.0); // Set invalid price

        // Act
        String result = addonController.createNewService(serviceEntity, redirectAttributes, session);

        // Assert
        assertEquals("redirect:/addon/new", result);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Price should be above '0'");
        verifyNoInteractions(serviceRepository);
    }
    
}
