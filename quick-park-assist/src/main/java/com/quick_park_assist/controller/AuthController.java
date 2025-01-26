package com.quick_park_assist.controller;


import com.quick_park_assist.entity.User;
import com.quick_park_assist.repository.UserRepository;
import com.quick_park_assist.service.IOTPService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {
    public static final String ERROR_MESSAGE = "errorMessage";
    public static final String SUCCESS_MESSAGE = "successMessage";
    public static final String EMAIL = "email";
    public static final String REDIRECT_AUTH_FORGOT = "redirect:/auth/forgot";
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
    public String forgotPassword(@RequestParam(EMAIL) String email, RedirectAttributes redirectAttributes, Model model) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {

            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "The Email is not Found!");
        } else {
            String response = otpService.sendOTP(userOpt.get());
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, response);
            redirectAttributes.addFlashAttribute("otpEnabled", true);
            redirectAttributes.addFlashAttribute(EMAIL, email);
        }
        return REDIRECT_AUTH_FORGOT;
    }
    @PostMapping("/verify-otp")
    @Transactional
    public String verifyOtp(@RequestParam(value = EMAIL,required = true) String email, @RequestParam String otp, RedirectAttributes redirectAttributes, Model model) {

        try {
            if (otpService.verifyOTP(email, otp)) {
                redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "OTP Verified Successfully. You can now reset your password.");
                redirectAttributes.addFlashAttribute(EMAIL, email);

                return "redirect:/auth/resetPassword?email=" + URLEncoder.encode(email, "UTF-8");
            } else {
                redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Invalid OTP or OTP has expired. Please try again.");
                redirectAttributes.addFlashAttribute("otpEnabled", true);
                redirectAttributes.addFlashAttribute(EMAIL, email);
            }
        }catch (UnsupportedEncodingException e){}
        return REDIRECT_AUTH_FORGOT;
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
                                RedirectAttributes redirectAttributes) {
        if (email == null || email.isEmpty()) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Email is missing. Please retry the process.");
            return "redirect:/auth/resetPassword";
        }
        if (otpService.resetPassword(email, newPassword)) {
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Password reset successfully. Please log in.");
            return "redirect:/login";
        } else {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Couldn't reset the password. Try again!");
            redirectAttributes.addFlashAttribute(EMAIL, email);
            return "redirect:/auth/resetPassword";
        }
    }

}

