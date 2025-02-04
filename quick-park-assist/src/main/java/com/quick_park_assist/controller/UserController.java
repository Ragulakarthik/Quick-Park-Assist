package com.quick_park_assist.controller;

import com.quick_park_assist.repository.UserRepository;
import com.quick_park_assist.service.IOTPService;
import com.quick_park_assist.service.IRegistrationOTPService;
import com.quick_park_assist.service.IUserService;
import com.quick_park_assist.serviceImpl.UserServiceImpl;
import com.quick_park_assist.util.OTPGenerator;
import jakarta.servlet.http.HttpSession;
import com.quick_park_assist.util.PasswordMatchValidator;
import com.quick_park_assist.dto.UserRegistrationDTO;
import com.quick_park_assist.entity.User;
import com.quick_park_assist.dto.UserProfileDTO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class UserController {

    // Add this line for logger
    private static final Logger log = LogManager.getLogger(UserController.class);
    public static final String REGISTRATION = "registration";
    public static final String PENDING_REGISTRATION = "pendingRegistration";
    public static final String REGISTRATION_VERIFY = "registration-verify";
    public static final String EMAIL = "email";
    public static final String ERROR_MESSAGE = "errorMessage";
    public static final String REDIRECT_REGISTER = "redirect:/register";
    public static final String USER_ID = "userId";
    public static final String USER_TYPE = "userType";
    public static final String REDIRECT_DASHBOARD = "redirect:/dashboard";
    public static final String LAST_OTP_RESEND_TIME = "lastOtpResendTime";
    public static final String SUCCESS_MESSAGE = "successMessage";
    public static final String REDIRECT_PROFILE_REACTIVATE = "redirect:/profile/reactivate";
    public static final String REDIRECT_LOGIN = "redirect:/login";
    public static final String USER_EMAIL = "userEmail";
    public static final String USER_FULL_NAME = "userFullName";
    public static final String OTP_ATTEMPTS = "otpAttempts";


    @Autowired
    private IUserService userService;

    @Autowired
    private PasswordMatchValidator passwordMatchValidator;

    @Autowired
    private IOTPService otpService;
    @Autowired
    private  IRegistrationOTPService registrationOTPService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OTPGenerator otpGenerator;

    @GetMapping("/")
    public String home() {
        return "home";
    }


    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new UserRegistrationDTO());
        }
        return REGISTRATION;
    }


    @PostMapping("/user-register")
    public String registerUser(@Valid @ModelAttribute("user") UserRegistrationDTO userDTO,
                               BindingResult result,
                               HttpSession session,
                               Model model) {
        try {
            log.info("Received registration request for email: {}", userDTO.getEmail());

            // Log validation state before custom validation
            log.info("Initial validation errors: {}", result.hasErrors());
            if (result.hasErrors()) {
                result.getAllErrors().forEach(error ->
                        log.info("Validation error: {}", error.getDefaultMessage())
                );
            }

            // Custom password match validation
            passwordMatchValidator.validate(userDTO, result);

            // Log validation state after password validation
            log.info("Validation errors after password check: {}", result.hasErrors());

            if (result.hasErrors()) {
                return REGISTRATION;
            }

            // Check for existing email and phone
            if (userService.isEmailTaken(userDTO.getEmail())) {
                result.rejectValue(EMAIL, "email.exists", "Email already registered");
            }
            if (userService.isPhoneNumberTaken(userDTO.getPhoneNumber())) {
                result.rejectValue("phoneNumber", "phone.exists", "Phone number already registered");
            }

            // Add this check for validation errors
            if (result.hasErrors()) {
                log.warn("Validation errors occurred: {}", result.getAllErrors());
                return REGISTRATION;  // Return to form with errors
            }

            // Store registration data in session for later use after OTP verification
            session.setAttribute(PENDING_REGISTRATION, userDTO);

            // Send registration OTP
            registrationOTPService.sendRegistrationOTP(userDTO.getEmail());

            model.addAttribute(EMAIL, userDTO.getEmail());
            return REGISTRATION_VERIFY;

        } catch (Exception e) {
            log.error("Error in registration: ", e);
            model.addAttribute(ERROR_MESSAGE, "An error occurred: " + e.getMessage());
            return REGISTRATION;
        }
    }

    @PostMapping("/verify-registration")
    public String verifyRegistration(@RequestParam String email,
                                     @RequestParam String otp,
                                     HttpSession session,
                                     Model model,
                                     RedirectAttributes redirectAttributes) throws UserServiceImpl.PasswordHashingException {

        UserRegistrationDTO userDTO = (UserRegistrationDTO) session.getAttribute(PENDING_REGISTRATION);

        if (userDTO == null) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Registration session expired. Please register again.");
            return REDIRECT_REGISTER;
        }

        // Get or initialize OTP attempts counter
        Integer attempts = (Integer) session.getAttribute(OTP_ATTEMPTS);
        if (attempts == null) {
            attempts = 0;
        }

        if (registrationOTPService.verifyRegistrationOTP(email, otp)) {
            // OTP verification successful
            User savedUser = userService.registerUser(userDTO);

            // Set session attributes
            session.setAttribute(USER_ID, savedUser.getId());
            session.setAttribute(USER_TYPE, savedUser.getUserType());
            session.setAttribute("loggedInUser", savedUser);

            // Clean up session
            session.removeAttribute(PENDING_REGISTRATION);
            session.removeAttribute(OTP_ATTEMPTS);

            return REDIRECT_DASHBOARD;
        } else {
            // Increment attempts counter
            attempts++;
            session.setAttribute(OTP_ATTEMPTS, attempts);

            if (attempts >= 3) {
                // Clean up session
                session.removeAttribute(PENDING_REGISTRATION);
                session.removeAttribute(OTP_ATTEMPTS);

                redirectAttributes.addFlashAttribute(ERROR_MESSAGE,
                        "Too many incorrect attempts. Please register again.");
                return REDIRECT_REGISTER;
            }

            // Still have attempts remaining
            model.addAttribute(ERROR_MESSAGE,
                    String.format("Invalid verification code. %d attempts remaining.", (3 - attempts)));
            model.addAttribute(EMAIL, email);
            return REGISTRATION_VERIFY;
        }
    }

    // In UserController
    @GetMapping("/resend-otp")
    public String resendOTP(HttpSession session,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        UserRegistrationDTO userDTO = (UserRegistrationDTO) session.getAttribute(PENDING_REGISTRATION);
        Long lastResendTime = (Long) session.getAttribute(LAST_OTP_RESEND_TIME);

        if (userDTO == null) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Registration session expired. Please register again.");
            return REDIRECT_REGISTER;
        }

        // Check cooldown (30 seconds)
        if (lastResendTime != null) {
            long timeElapsed = System.currentTimeMillis() - lastResendTime;
            if (timeElapsed < 30000) { // 30 seconds
                model.addAttribute(ERROR_MESSAGE, "Please wait before requesting a new code");
                model.addAttribute(EMAIL, userDTO.getEmail());
                return REGISTRATION_VERIFY;
            }
        }

        try {
            // Send new OTP
            registrationOTPService.sendRegistrationOTP(userDTO.getEmail());

            // Update resend timestamp
            session.setAttribute(LAST_OTP_RESEND_TIME, System.currentTimeMillis());

            model.addAttribute(SUCCESS_MESSAGE, "New verification code sent");
            model.addAttribute(EMAIL, userDTO.getEmail());
            return REGISTRATION_VERIFY;
        } catch (Exception e) {
            log.error(e.toString());
            model.addAttribute(ERROR_MESSAGE, "Failed to send new code. Please try again.");
            model.addAttribute(EMAIL, userDTO.getEmail());
            return REGISTRATION_VERIFY;
        }
    }

    @GetMapping("/profile/resend-reactivation-otp")
    public String resendReactivationOTP(@RequestParam String email,
                                        HttpSession session,
                                        RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty() || userOpt.get().isActive()) {
            return REDIRECT_LOGIN;
        }

        Long lastResendTime = (Long) session.getAttribute(LAST_OTP_RESEND_TIME);
        if (lastResendTime != null && System.currentTimeMillis() - lastResendTime < 30000) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Please wait before requesting new code");
            redirectAttributes.addFlashAttribute(EMAIL, email);
            return REDIRECT_PROFILE_REACTIVATE;
        }

        otpService.sendReactivationOTP(userOpt.get());
        session.setAttribute(LAST_OTP_RESEND_TIME, System.currentTimeMillis());
        redirectAttributes.addFlashAttribute(EMAIL, email);
        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "New OTP sent to your email");
        return REDIRECT_PROFILE_REACTIVATE;
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        log.info("Showing login page");
        // Add any necessary attributes to the model
        return "login";
    }

    @PostMapping("/user-login")
    public String loginUser(@RequestParam String email,
                            @RequestParam String password,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        try {
            // Add debug logs
            log.info("Login attempt - Email: {}",email);

            User user = userService.authenticateUser(email, password);

            // Add debug log for user object
            log.info("Authentication result - User: {}", (user != null ? user.toString() : "null"));

            if (user != null) {
                if (!user.isActive()) {
                    otpService.sendReactivationOTP(user);
                    redirectAttributes.addFlashAttribute(EMAIL, email);
                    return REDIRECT_PROFILE_REACTIVATE;
                }
                session.setAttribute(USER_ID, user.getId());
                session.setAttribute(USER_EMAIL, user.getEmail());
                session.setAttribute(USER_FULL_NAME, user.getFullName());
                session.setAttribute("loggedInUser", user);
                session.setAttribute(USER_TYPE, user.getUserType());
                session.setAttribute("userIsThere", true);

                // Add debug log for session
                log.info("Session attributes set - UserType: {} " , session.getAttribute(USER_TYPE));

                return REDIRECT_DASHBOARD;
            } else {
                // Add debug log for failed login
                log.info("Login failed - Invalid credentials");

                redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Invalid email or password");
                return "redirect:/login?error";
            }
        } catch (UserServiceImpl.PasswordHashingException e) {
            // Add debug log for exceptions
            log.error("Login error: {}" , e.getMessage());

            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "An error occurred during login");
            return "redirect:/login?error";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "You have been successfully logged out");
        return "redirect:/login?logout";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        // Check if user is logged in
        if (session.getAttribute(USER_ID) == null) {
            return REDIRECT_LOGIN;
        }

        // Add user information to model
        model.addAttribute(USER_FULL_NAME, session.getAttribute(USER_FULL_NAME));
        return "dashboard";  // You'll need to create this view
    }

    @GetMapping("/profileAction")
    public String viewProfile(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute(USER_ID);
        if (userId == null) {
            return REDIRECT_LOGIN;
        }

        User user = userService.getUserById(userId);
        if (user == null) {
            return REDIRECT_LOGIN;
        }

        model.addAttribute("user", user);
        return "DeleteProfile";
    }

    @GetMapping("/profile/edit")
    public String showEditProfileForm(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute(USER_ID);
        if (userId == null) {
            return REDIRECT_LOGIN;
        }

        User user = userService.getUserById(userId);
        if (user == null) {
            return REDIRECT_LOGIN;
        }

        UserProfileDTO profileDTO = new UserProfileDTO();
        profileDTO.setFullName(user.getFullName());
        profileDTO.setEmail(user.getEmail());
        profileDTO.setPhoneNumber(user.getPhoneNumber());
        profileDTO.setAddress(user.getAddress());

        model.addAttribute("userProfile", profileDTO);
        model.addAttribute(USER_EMAIL, user.getEmail());  // Add this line
        return "EditProfile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@Valid @ModelAttribute("userProfile") UserProfileDTO profileDTO,
                                BindingResult result,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute(USER_ID);
        if (userId == null) {
            return REDIRECT_LOGIN;
        }
        try{
            if (result.hasErrors()) {
                return "EditProfile";
            }
        }catch (NullPointerException ne){
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Error updating profile: Error");
            log.error(ne.getMessage());
            return "redirect:/profile/edit";
        }
        try {
            userService.updateProfile(userId, profileDTO);
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Profile updated successfully!");
            return REDIRECT_DASHBOARD;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Error updating profile: ERROR ");
            return "redirect:/profile/edit";
        }
    }

    @PostMapping("/profile/deactivate")
    public String deactivateAccount(HttpSession session, RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute(USER_ID);
        String userEmail = (String) session.getAttribute(USER_EMAIL);
        if (userId == null) {
            return REDIRECT_LOGIN;
        }
        try {
            userService.deactivateAccount(userId);
            otpService.sendAccountStatusEmail(userEmail, true);
            session.invalidate();
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Account deactivated successfully");
            return REDIRECT_LOGIN;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Error deactivating account: " + e.getMessage());
            return "redirect:/profileAction";
        }
    }

    @PostMapping("/profile/delete")
    public String deleteAccount(HttpSession session, RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute(USER_ID);
        if (userId == null) {
            return REDIRECT_LOGIN;
        }
        try {
            userService.deleteAccount(userId);
            session.invalidate();
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Account deleted successfully");
            return REDIRECT_LOGIN;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Error deleting account: " + e.getMessage());
            return "redirect:/profileAction";
        }
    }

    @GetMapping("/profile/change-password")
    public String showChangePasswordForm() {
        return "changePassword";
    }

    @GetMapping("/profile/reactivate")
    public String showReactivationPage() {
        return "reactivate";
    }


    @PostMapping("/profile/verify-reactivation")
    public String verifyReactivation(@RequestParam String email,
                                     @RequestParam String otp,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        Integer attempts = (Integer) session.getAttribute(OTP_ATTEMPTS);
        if (attempts == null) attempts = 0;

        if (attempts >= 2) {
            session.removeAttribute(OTP_ATTEMPTS);
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Too many incorrect attempts. Please try again.");
            return REDIRECT_LOGIN;
        }
        if (otpService.verifyReactivationOTP(email, otp)) {
            session.removeAttribute(OTP_ATTEMPTS);
            userService.reactivateAccount(email);
            otpService.sendAccountStatusEmail(email, false); // false for reactivation
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Account reactivated successfully. Please login.");
            return REDIRECT_LOGIN;
        }
        attempts++;
        session.setAttribute(OTP_ATTEMPTS, attempts);
        redirectAttributes.addFlashAttribute(ERROR_MESSAGE,
                String.format("Invalid OTP. %d attempts remaining", (3 - attempts)));
        redirectAttributes.addFlashAttribute(EMAIL, email);
        return REDIRECT_PROFILE_REACTIVATE;
    }

}