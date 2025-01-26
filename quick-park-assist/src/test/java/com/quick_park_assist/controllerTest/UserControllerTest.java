package com.quick_park_assist.controllerTest;

import com.quick_park_assist.controller.UserController;
import com.quick_park_assist.dto.UserProfileDTO;
import com.quick_park_assist.dto.UserRegistrationDTO;
import com.quick_park_assist.entity.User;
import com.quick_park_assist.service.IUserService;
import com.quick_park_assist.util.PasswordMatchValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserControllerTest {


    @Mock
    private IUserService userService;

    @Mock
    private PasswordMatchValidator passwordMatchValidator;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;
    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testShowRegistrationForm() {
        // Act
        String viewName = userController.showRegistrationForm(model);

        // Assert
        assertEquals("registration", viewName);
        verify(model).addAttribute(eq("user"), any(UserRegistrationDTO.class));
    }

    @Test
    void testRegisterUser_Success() {
        // Arrange
        UserRegistrationDTO userDTO = new UserRegistrationDTO();
        userDTO.setEmail("test@example.com");
        userDTO.setPhoneNumber("1234567890");
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
        mockUser.setUserType("Regular");

        when(userService.isEmailTaken(userDTO.getEmail())).thenReturn(false);
        when(userService.isPhoneNumberTaken(userDTO.getPhoneNumber())).thenReturn(false);
        when(userService.registerUser(userDTO)).thenReturn(mockUser);

        // Act
        String viewName = userController.registerUser(userDTO, bindingResult, session, model);

        // Assert
        assertEquals("redirect:/dashboard", viewName);
        verify(session).setAttribute("userId", mockUser.getId());
        verify(session).setAttribute("userType", mockUser.getUserType());
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() {
        // Arrange
        UserRegistrationDTO userDTO = new UserRegistrationDTO();
        userDTO.setEmail("test@example.com");
        when(userService.isEmailTaken(userDTO.getEmail())).thenReturn(true);

        // Act
        String viewName = userController.registerUser(userDTO, bindingResult, session, model);

        // Assert
        assertEquals("registration", viewName);
        verify(bindingResult).rejectValue("email", "email.exists", "Email already registered");
    }

    @Test
    void testRegisterUser_WithValidationErrors() {
        // Arrange
        UserRegistrationDTO userDTO = new UserRegistrationDTO();
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String viewName = userController.registerUser(userDTO, bindingResult, session, model);

        // Assert
        assertEquals("registration", viewName);
        verify(bindingResult).hasErrors();
    }

    @Test
    void testRegisterUser_ExceptionHandling() {
        // Arrange
        UserRegistrationDTO userDTO = new UserRegistrationDTO();
        userDTO.setEmail("test@example.com");
        when(userService.registerUser(userDTO)).thenThrow(new RuntimeException("Database error"));

        // Act
        String viewName = userController.registerUser(userDTO, bindingResult, session, model);

        // Assert
        assertEquals("registration", viewName);
        verify(model).addAttribute(eq("errorMessage"), anyString());
    }
    @Test
    void testShowLoginPage() {
        String viewName = userController.showLoginPage(model);
        assertEquals("login", viewName);
    }

    @Test
    void testLoginUser_Success() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
        mockUser.setFullName("John Doe");
        mockUser.setUserType("Regular");

        when(userService.authenticateUser("test@example.com", "password")).thenReturn(mockUser);

        String viewName = userController.loginUser("test@example.com", "password", session, model, redirectAttributes);

        assertEquals("dashboard", viewName);
        verify(session).setAttribute("userId", mockUser.getId());
        verify(session).setAttribute("userEmail", mockUser.getEmail());
        verify(session).setAttribute("userFullName", mockUser.getFullName());
    }

    @Test
    void testLoginUser_InvalidCredentials() {
        when(userService.authenticateUser("test@example.com", "password")).thenReturn(null);

        String viewName = userController.loginUser("test@example.com", "password", session, model, redirectAttributes);

        assertEquals("redirect:/login?error", viewName);
        verify(redirectAttributes).addFlashAttribute("error", "Invalid email or password");
    }

    @Test
    void testLogout() {
        String viewName = userController.logout(session, redirectAttributes);

        assertEquals("redirect:/login?logout", viewName);
        verify(session).invalidate();
        verify(redirectAttributes).addFlashAttribute("successMessage", "You have been successfully logged out");
    }

    @Test
    void testViewProfile_UserLoggedIn() {
        User mockUser = new User();
        mockUser.setId(1L);
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(mockUser);

        String viewName = userController.viewProfile(session, model);

        assertEquals("DeleteProfile", viewName);
        verify(model).addAttribute("user", mockUser);
    }

    @Test
    void testViewProfile_UserNotLoggedIn() {
        when(session.getAttribute("userId")).thenReturn(null);

        String viewName = userController.viewProfile(session, model);

        assertEquals("redirect:/login", viewName);
    }

    @Test
    void testUpdateProfile_Success() {
        UserProfileDTO profileDTO = new UserProfileDTO();
        when(session.getAttribute("userId")).thenReturn(1L);
        when(bindingResult.hasErrors()).thenReturn(false);

        String viewName = userController.updateProfile(profileDTO, bindingResult, session, redirectAttributes);

        assertEquals("redirect:/dashboard", viewName);
        verify(userService).updateProfile(eq(1L), eq(profileDTO));
        verify(redirectAttributes).addFlashAttribute("successMessage", "Profile updated successfully!");
    }

    @Test
    void testUpdateProfile_WithValidationErrors() {
        // Arrange
        UserProfileDTO profileDTO = new UserProfileDTO();
        when(bindingResult.hasErrors()).thenReturn(true);
        when(session.getAttribute("userId")).thenReturn(1L); // Mock user is logged in

        // Act
        String viewName = userController.updateProfile(profileDTO, bindingResult, session, redirectAttributes);

        // Assert
        assertEquals("EditProfile", viewName);  // Expect returning to EditProfile view
        verify(bindingResult).hasErrors();      // Ensure validation errors are checked
        verifyNoInteractions(userService);      // Ensure the service is not called
    }




    @Test
    void testDeactivateAccount_Success() {
        when(session.getAttribute("userId")).thenReturn(1L);

        String viewName = userController.deactivateAccount(session, redirectAttributes);

        assertEquals("redirect:/login", viewName);
        verify(userService).deactivateAccount(eq(1L));
        verify(session).invalidate();
        verify(redirectAttributes).addFlashAttribute("successMessage", "Account deactivated successfully");
    }

    @Test
    void testDeleteAccount_Success() {
        when(session.getAttribute("userId")).thenReturn(1L);

        String viewName = userController.deleteAccount(session, redirectAttributes);

        assertEquals("redirect:/login", viewName);
        verify(userService).deleteAccount(eq(1L));
        verify(session).invalidate();
        verify(redirectAttributes).addFlashAttribute("successMessage", "Account deleted successfully");
    }
}

