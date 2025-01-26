package com.quick_park_assist.controllerTest;

import com.quick_park_assist.controller.AuthController;
import com.quick_park_assist.entity.User;
import com.quick_park_assist.repository.UserRepository;
import com.quick_park_assist.service.IOTPService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private IOTPService otpService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private Model model;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test case 1: Forgot Password - Email not found
    @Test
    void testForgotPassword_EmailNotFound() {
        // Arrange
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        String viewName = authController.forgotPassword(email, redirectAttributes, model);

        // Assert
        assertEquals("redirect:/auth/forgot", viewName);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "The Email is not Found!");
    }

    // Test case 2: Forgot Password - OTP sent successfully
    @Test
    void testForgotPassword_OTPSentSuccessfully() {
        // Arrange
        String email = "test@example.com";
        User user = new User();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(otpService.sendOTP(user)).thenReturn("OTP Successfully Sent");

        // Act
        String viewName = authController.forgotPassword(email, redirectAttributes, model);

        // Assert
        assertEquals("redirect:/auth/forgot", viewName);
        verify(redirectAttributes).addFlashAttribute("successMessage", "OTP Successfully Sent");
        verify(redirectAttributes).addFlashAttribute("otpEnabled", true);
        verify(redirectAttributes).addFlashAttribute("email", email);
    }

    // Test case 3: Verify OTP - Success
    @Test
    void testVerifyOtp_Success()  {
            // Arrange
            String email = "test@example.com";
            String otp = "123456";
            when(otpService.verifyOTP(email, otp)).thenReturn(true);

            // Act
            String viewName = authController.verifyOtp(email, otp, redirectAttributes, model);

            // Assert
            assertEquals("redirect:/auth/resetPassword?email=test%40example.com", viewName);
            verify(redirectAttributes).addFlashAttribute("successMessage", "OTP Verified Successfully. You can now reset your password.");
            verify(redirectAttributes).addFlashAttribute("email", email);

    }

    // Test case 4: Verify OTP - Failure
    @Test
    void testVerifyOtp_Failure() {
        // Arrange
        String email = "test@example.com";
        String otp = "123456";
        when(otpService.verifyOTP(email, otp)).thenReturn(false);

        // Act
        String viewName = authController.verifyOtp(email, otp, redirectAttributes, model);

        // Assert
        assertEquals("redirect:/auth/forgot", viewName);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Invalid OTP or OTP has expired. Please try again.");
        verify(redirectAttributes).addFlashAttribute("otpEnabled", true);
        verify(redirectAttributes).addFlashAttribute("email", email);
    }

    // Test case 5: Reset Password - Success
    @Test
    void testResetPassword_Success() {
        // Arrange
        String email = "test@example.com";
        String newPassword = "newPassword123";
        when(otpService.resetPassword(email, newPassword)).thenReturn(true);

        // Act
        String viewName = authController.resetPassword(email, newPassword, redirectAttributes);

        // Assert
        assertEquals("redirect:/login", viewName);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Password reset successfully. Please log in.");
    }

    // Test case 6: Reset Password - Failure
    @Test
    void testResetPassword_Failure() {
        // Arrange
        String email = "test@example.com";
        String newPassword = "newPassword123";
        when(otpService.resetPassword(email, newPassword)).thenReturn(false);

        // Act
        String viewName = authController.resetPassword(email, newPassword, redirectAttributes);

        // Assert
        assertEquals("redirect:/auth/resetPassword", viewName);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Couldn't reset the password. Try again!");
        verify(redirectAttributes).addFlashAttribute("email", email);
    }

    // Test case 7: Reset Password - Missing Email
    @Test
    void testResetPassword_MissingEmail() {
        // Arrange
        String email = null;
        String newPassword = "newPassword123";

        // Act
        String viewName = authController.resetPassword(email, newPassword, redirectAttributes);

        // Assert
        assertEquals("redirect:/auth/resetPassword", viewName);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Email is missing. Please retry the process.");
    }
}
