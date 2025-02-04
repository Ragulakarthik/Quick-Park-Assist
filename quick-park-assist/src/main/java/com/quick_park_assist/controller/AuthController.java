package com.quick_park_assist.controller;


import com.quick_park_assist.entity.User;
import com.quick_park_assist.repository.UserRepository;
import com.quick_park_assist.service.IOTPService;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;



@Controller
@RequestMapping("/auth")
public class AuthController {
    public static final String REDIRECT_AUTH_FORGOT = "redirect:/auth/forgot";
    public static final String SUCCESS_MESSAGE = "successMessage";
    public static final String EMAIL = "email";
    public static final String ERROR_MESSAGE = "errorMessage";
    public static final String REDIRECT_AUTH_CHANGE_PASSWORD = "redirect:/auth/change-password";
    public static final String REDIRECT_AUTH_RESET_PASSWORD_EMAIL = "redirect:/auth/resetPassword?email=";
    public static final String REDIRECT_LOGIN = "redirect:/login";
    public static final String OTP_ATTEMPTS = "otpAttempts";

    Logger log = LogManager.getLogger(AuthController.class);
    @Autowired
    private IOTPService otpService;
    @Autowired
    UserRepository userRepository;
    @GetMapping("/forgot")
    public String showForgotPasswordForm(){
        return "forgotPassword";
    }

    @PostMapping("/forgot-password")
    @Transactional
    public String forgotPassword(
            @RequestParam(EMAIL) String email,
            RedirectAttributes redirectAttributes,
            HttpSession session,
            Model model) {

        String redirectUrl = REDIRECT_AUTH_FORGOT;

        try {
            Optional<User> userOpt = userRepository.findByEmail(email);

            // Handle case where email is not found
            if (userOpt.isEmpty()) {
                setFlashAttributes(redirectAttributes, ERROR_MESSAGE, "Email not found!", email);
            } else if (isOtpResendTooSoon(session)) {
                // Check for resend timeout
                setFlashAttributes(redirectAttributes, ERROR_MESSAGE, "Please wait before requesting a new code", email);
            } else {
                // Send OTP
                String response = otpService.sendOTP(userOpt.get());
                log.info(response);

                // Update session and flash attributes
                session.setAttribute("lastOtpResendTime", System.currentTimeMillis());
                setFlashAttributes(redirectAttributes, SUCCESS_MESSAGE, "OTP sent to your email", email);
            }

        } catch (RuntimeException e) {
            log.error("Error during forgotPassword process", e);
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "An unexpected error occurred. Please try again.");
        }

        return redirectUrl;
    }

    /**
     * Checks if the OTP resend request is being made too soon.
     *
     * @param session The current HTTP session
     * @return true if the request is too soon, false otherwise
     */
    private boolean isOtpResendTooSoon(HttpSession session) {
        Long lastResendTime = (Long) session.getAttribute("lastOtpResendTime");
        if (lastResendTime == null) {
            return false;
        }
        return System.currentTimeMillis() - lastResendTime < 30000;
    }

    /**
     * Sets flash attributes for redirect scenarios.
     *
     * @param redirectAttributes The RedirectAttributes object
     * @param messageType         The type of message (e.g., success or error)
     * @param message             The message to be displayed
     * @param email               The email to retain in flash attributes
     */
    private void setFlashAttributes(RedirectAttributes redirectAttributes, String messageType, String message, String email) {
        redirectAttributes.addFlashAttribute(messageType, message);
        redirectAttributes.addFlashAttribute(EMAIL, email);
    }

    @PostMapping("/verify-otp")
    @Transactional
    public String verifyOtp(@RequestParam(value = EMAIL,required = true) String email, @RequestParam String otp, HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        log.info(email);
        try {
            Integer attempts = (Integer) session.getAttribute(OTP_ATTEMPTS);
            if (attempts == null) attempts = 0;

            if (attempts >= 2) {
                session.removeAttribute(OTP_ATTEMPTS);
                redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Too many incorrect attempts. Please try again.");
                return REDIRECT_LOGIN;
            }

            if (otpService.verifyOTP(email, otp)) {
                session.removeAttribute(OTP_ATTEMPTS);
                redirectAttributes.addFlashAttribute(EMAIL, email);
                return REDIRECT_AUTH_RESET_PASSWORD_EMAIL + email;
            } else {
                attempts++;
                session.setAttribute(OTP_ATTEMPTS, attempts);
                redirectAttributes.addFlashAttribute(ERROR_MESSAGE,
                        String.format("Invalid OTP. %d attempts remaining", (3 - attempts)));
                redirectAttributes.addFlashAttribute(EMAIL, email);
                return REDIRECT_AUTH_FORGOT;
            }
        }
        catch (Exception e){
            log.error(e.toString());
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Verification failed. Please try again.");
            return REDIRECT_LOGIN;}
    }
    @GetMapping("/resetPassword")
    public String showResetPasswordForm(@RequestParam(value = EMAIL, required = true) String email, Model model, RedirectAttributes redirectAttributes) {
        if (email == null || email.isEmpty()) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Email is missing. Please restart the process.");
            return REDIRECT_AUTH_FORGOT; // Replace with your actual error page
        }
        model.addAttribute(EMAIL, email);
        return "newPassword";
    }


    @PostMapping("/reset-password")
    @Transactional
    public String resetPassword(@RequestParam(value = EMAIL, required = false) String email,
                                @RequestParam String newPassword,
                                @RequestParam String confirmPassword,
                                RedirectAttributes redirectAttributes) {
        if (email == null || email.isEmpty()) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Email is missing. Please retry the process.");
            return "redirect:/auth/resetPassword";
        }
        // Validate password requirements
        if (!newPassword.matches("^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=]).*$") || newPassword.length() < 6) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE,
                    "Password must be at least 6 characters and contain at least one letter, one number, and one special character");
            redirectAttributes.addFlashAttribute(EMAIL, email);
            return REDIRECT_AUTH_RESET_PASSWORD_EMAIL + email;
        }

        // Check if passwords match
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Passwords do not match");
            redirectAttributes.addFlashAttribute(EMAIL, email);
            return REDIRECT_AUTH_RESET_PASSWORD_EMAIL + email;
        }
        if (otpService.resetPassword(email, newPassword)) {
            otpService.sendPasswordChangeEmail(email);
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Password reset successfully. Please log in.");
            return REDIRECT_LOGIN;
        } else {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Couldn't reset the password. Try again!");
            redirectAttributes.addFlashAttribute(EMAIL, email);
            return "redirect:/auth/resetPassword";
        }
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm(HttpSession session) {
        if (session.getAttribute("userId") == null) {
            return REDIRECT_LOGIN;
        }
        return "changePassword";
    }

    @PostMapping("/change-password")
    @Transactional
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("userId");
        String userEmail = (String) session.getAttribute("userEmail");

        if (userId == null) {
            return REDIRECT_LOGIN;
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty() || !userOpt.get().getPassword().equals(currentPassword)) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Current password is incorrect");
            return REDIRECT_AUTH_CHANGE_PASSWORD;
        }

        if (!newPassword.matches("^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=]).*$") || newPassword.length() < 6) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE,
                    "Password must be at least 6 characters and contain at least one letter, one number, and one special character");
            return REDIRECT_AUTH_CHANGE_PASSWORD;
        }

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Passwords do not match");
            return REDIRECT_AUTH_CHANGE_PASSWORD;
        }

        if (otpService.resetPassword(userEmail, newPassword)) {
            otpService.sendPasswordChangeEmail(userEmail);
            session.invalidate();
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Password changed successfully. Please login again.");
            return REDIRECT_LOGIN;
        }

        redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Failed to change password. Please try again.");
        return REDIRECT_AUTH_CHANGE_PASSWORD;
    }

}

