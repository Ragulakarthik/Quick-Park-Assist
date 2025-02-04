package com.quick_park_assist.controllerTest;

import com.quick_park_assist.controller.AuthController;
import com.quick_park_assist.entity.User;
import com.quick_park_assist.repository.UserRepository;
import com.quick_park_assist.service.IOTPService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

import static com.quick_park_assist.controller.AuthController.ERROR_MESSAGE;
import static com.quick_park_assist.controller.AuthController.REDIRECT_AUTH_CHANGE_PASSWORD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private IOTPService otpService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testShowChangePasswordForm_SessionNull(){
        when(session.getAttribute("userId")).thenReturn(null);
        String viewName = authController.showChangePasswordForm(session);
        assertEquals("redirect:/login",viewName);
    }
    @Test
    void testShowPasswordForm(){
        when(session.getAttribute("userId")).thenReturn(1L);
        String viewName = authController.showChangePasswordForm(session);
        assertEquals("changePassword",viewName);
    }
    @Test
    void testShowForgotPasswordForm() {
        String viewName = authController.showForgotPasswordForm();
        assertEquals("forgotPassword", viewName);
    }

    @Test
    void testForgotPassword_UserNotFound() {
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        String viewName = authController.forgotPassword(email, redirectAttributes, session, model);

        assertEquals("redirect:/auth/forgot", viewName);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Email not found!");
    }

    @Test
    void testForgotPassword_CooldownPeriod() {
        String email = "test@example.com";
        long currentTime = System.currentTimeMillis();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new User()));
        when(session.getAttribute("lastOtpResendTime")).thenReturn(currentTime - 15000); // 15 seconds ago

        String viewName = authController.forgotPassword(email, redirectAttributes, session, model);

        assertEquals("redirect:/auth/forgot", viewName);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Please wait before requesting a new code");
    }

    @Test
    void testForgotPassword_Success() {
        String email = "test@example.com";
        User user = new User();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(otpService.sendOTP(user)).thenReturn("OTP sent successfully");

        String viewName = authController.forgotPassword(email, redirectAttributes, session, model);

        assertEquals("redirect:/auth/forgot", viewName);
        verify(redirectAttributes).addFlashAttribute("successMessage", "OTP sent to your email");
        verify(session).setAttribute(eq("lastOtpResendTime"), anyLong());
    }

    @Test
    void testVerifyOtp_Success() {
        String email = "test@example.com";
        String otp = "123456";
        when(otpService.verifyOTP(email, otp)).thenReturn(true);

        String viewName = authController.verifyOtp(email, otp, session, redirectAttributes, model);

        assertEquals("redirect:/auth/resetPassword?email=" + email, viewName);
        verify(session).removeAttribute("otpAttempts");
    }

    @Test
    void testVerifyOtp_InvalidOTP() {
        String email = "test@example.com";
        String otp = "123456";
        when(otpService.verifyOTP(email, otp)).thenReturn(false);
        when(session.getAttribute("otpAttempts")).thenReturn(0);

        String viewName = authController.verifyOtp(email, otp, session, redirectAttributes, model);

        assertEquals("redirect:/auth/forgot", viewName);
        verify(session).setAttribute("otpAttempts", 1);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Invalid OTP. 2 attempts remaining");
    }

    @Test
    void testVerifyOtp_MaxAttemptsReached() {
        String email = "test@example.com";
        String otp = "123456";
        when(session.getAttribute("otpAttempts")).thenReturn(2);

        String viewName = authController.verifyOtp(email, otp, session, redirectAttributes, model);

        assertEquals("redirect:/login", viewName);
        verify(session).removeAttribute("otpAttempts");
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Too many incorrect attempts. Please try again.");
    }

    @Test
    void testResetPassword_Success() {
        String email = "test@example.com";
        String newPassword = "NewPass@123";
        String confirmPassword = "NewPass@123";
        when(otpService.resetPassword(email, newPassword)).thenReturn(true);

        String viewName = authController.resetPassword(email, newPassword, confirmPassword, redirectAttributes);

        assertEquals("redirect:/login", viewName);
        verify(otpService).sendPasswordChangeEmail(email);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Password reset successfully. Please log in.");
    }

    @Test
    void testResetPassword_PasswordMismatch() {
        String email = "test@example.com";
        String newPassword = "NewPass@123";
        String confirmPassword = "DifferentPass@123";

        String viewName = authController.resetPassword(email, newPassword, confirmPassword, redirectAttributes);

        assertEquals("redirect:/auth/resetPassword?email=" + email, viewName);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Passwords do not match");
    }

    @Test
    void testResetPassword_InvalidPassword() {
        String email = "test@example.com";
        String newPassword = "weak";
        String confirmPassword = "weak";

        String viewName = authController.resetPassword(email, newPassword, confirmPassword, redirectAttributes);

        assertEquals("redirect:/auth/resetPassword?email=" + email, viewName);
        verify(redirectAttributes).addFlashAttribute("errorMessage",
                "Password must be at least 6 characters and contain at least one letter, one number, and one special character");
    }

    @Test
    void testChangePassword_Success() {
        Long userId = 1L;
        String userEmail = "test@example.com";
        String currentPassword = "OldPass@123";
        String newPassword = "NewPass@123";
        String confirmPassword = "NewPass@123";

        User user = new User();
        user.setPassword(currentPassword);

        when(session.getAttribute("userId")).thenReturn(userId);
        when(session.getAttribute("userEmail")).thenReturn(userEmail);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(otpService.resetPassword(userEmail, newPassword)).thenReturn(true);

        String viewName = authController.changePassword(currentPassword, newPassword, confirmPassword,
                session, redirectAttributes);

        assertEquals("redirect:/login", viewName);
        verify(otpService).sendPasswordChangeEmail(userEmail);
        verify(session).invalidate();
    }

    @Test
    void testChangePassword_InvalidCurrentPassword() {
        Long userId = 1L;
        String currentPassword = "WrongPass@123";
        String newPassword = "NewPass@123";
        String confirmPassword = "NewPass@123";

        User user = new User();
        user.setPassword("ActualPass@123");

        when(session.getAttribute("userId")).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        String viewName = authController.changePassword(currentPassword, newPassword, confirmPassword,
                session, redirectAttributes);

        assertEquals("redirect:/auth/change-password", viewName);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Current password is incorrect");
    }

    @Test
    void testVerifyOtp_HandleUnexpectedException() {
        String email = "test@example.com";
        String otp = "123456";
        when(otpService.verifyOTP(email, otp)).thenThrow(new RuntimeException("Verification failed"));

        String viewName = authController.verifyOtp(email, otp, session, redirectAttributes, model);

        assertEquals("redirect:/login", viewName);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Verification failed. Please try again.");
    }
    @Test
    void testResetPassword_MissingEmail() {
        String newPassword = "NewPass@123";
        String confirmPassword = "NewPass@123";

        String viewName = authController.resetPassword(null, newPassword, confirmPassword, redirectAttributes);

        assertEquals("redirect:/auth/resetPassword", viewName);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Email is missing. Please retry the process.");
    }
    @Test
    void testResetPassword_ResetFailed() {
        String email = "test@example.com";
        String newPassword = "NewPass@123";
        String confirmPassword = "NewPass@123";

        when(otpService.resetPassword(email, newPassword)).thenReturn(false);

        String viewName = authController.resetPassword(email, newPassword, confirmPassword, redirectAttributes);

        assertEquals("redirect:/auth/resetPassword", viewName);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Couldn't reset the password. Try again!");
        verify(redirectAttributes).addFlashAttribute("email", email);
    }
    @Test
    void testChangePassword_MissingUserId() {
        String currentPassword = "OldPass@123";
        String newPassword = "NewPass@123";
        String confirmPassword = "NewPass@123";

        when(session.getAttribute("userId")).thenReturn(null);

        String viewName = authController.changePassword(currentPassword, newPassword, confirmPassword, session, redirectAttributes);

        assertEquals("redirect:/login", viewName);
        verify(redirectAttributes, never()).addFlashAttribute(anyString(), anyString());
    }
    @Test
    void testChangePassword_ResetFailed() {
        Long userId = 1L;
        String userEmail = "test@example.com";
        String currentPassword = "OldPass@123";
        String newPassword = "NewPass@123";
        String confirmPassword = "NewPass@123";

        User user = new User();
        user.setPassword(currentPassword);

        when(session.getAttribute("userId")).thenReturn(userId);
        when(session.getAttribute("userEmail")).thenReturn(userEmail);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(otpService.resetPassword(userEmail, newPassword)).thenReturn(false);

        String viewName = authController.changePassword(currentPassword, newPassword, confirmPassword, session, redirectAttributes);

        assertEquals("redirect:/auth/change-password", viewName);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Failed to change password. Please try again.");
    }
    @Test
    void testShowResetPasswordForm_MissingEmail() {
        String viewName = authController.showResetPasswordForm(null, model, redirectAttributes);

        assertEquals("redirect:/auth/forgot", viewName);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Email is missing. Please restart the process.");
    }

    // Test case: Incorrect current password
    @Test
    void testChangePassword_IncorrectCurrentPassword() {
        // Arrange
        Long userId = 1L;
        String currentPassword = "oldPass123!";
        String newPassword = "NewPass@123";
        String confirmPassword = "NewPass@123";

        User user = new User();
        user.setPassword("WrongPassword");

        when(session.getAttribute("userId")).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        String viewName = authController.changePassword(currentPassword, newPassword, confirmPassword, session, redirectAttributes);

        // Assert
        assertEquals(REDIRECT_AUTH_CHANGE_PASSWORD, viewName);
        verify(redirectAttributes).addFlashAttribute(ERROR_MESSAGE, "Current password is incorrect");
    }

    // Test case: Weak new password (doesn't meet complexity requirements)
    @Test
    void testChangePassword_WeakNewPassword() {
        // Arrange
        Long userId = 1L;
        String currentPassword = "oldPass123!";
        String newPassword = "weak"; // Doesn't meet requirements
        String confirmPassword = "weak";

        User user = new User();
        user.setPassword(currentPassword);

        when(session.getAttribute("userId")).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        String viewName = authController.changePassword(currentPassword, newPassword, confirmPassword, session, redirectAttributes);

        // Assert
        assertEquals(REDIRECT_AUTH_CHANGE_PASSWORD, viewName);
        verify(redirectAttributes).addFlashAttribute(ERROR_MESSAGE,
                "Password must be at least 6 characters and contain at least one letter, one number, and one special character");
    }

    // Test case: Passwords do not match
    @Test
    void testChangePassword_PasswordsDoNotMatch() {
        // Arrange
        Long userId = 1L;
        String currentPassword = "oldPass123!";
        String newPassword = "NewPass@123";
        String confirmPassword = "NewPass@124"; // Different confirmation password

        User user = new User();
        user.setPassword(currentPassword);

        when(session.getAttribute("userId")).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        String viewName = authController.changePassword(currentPassword, newPassword, confirmPassword, session, redirectAttributes);

        // Assert
        assertEquals(REDIRECT_AUTH_CHANGE_PASSWORD, viewName);
        verify(redirectAttributes).addFlashAttribute(ERROR_MESSAGE, "Passwords do not match");
    }

    // Test case: Successful password change

}