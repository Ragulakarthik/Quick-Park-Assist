package com.quick_park_assist.controller;
import com.quick_park_assist.service.IUserService;
import jakarta.servlet.http.HttpSession;
import com.quick_park_assist.util.PasswordMatchValidator;
import com.quick_park_assist.dto.UserRegistrationDTO;
import com.quick_park_assist.entity.User;
import com.quick_park_assist.dto.UserProfileDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    // Add this line for logger
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    public static final String USER_FULL_NAME = "userFullName";
    public static final String USER_ID = "userId";
    public static final String REGISTRATION = "registration";
    public static final String SUCCESS_MESSAGE = "successMessage";
    public static final String ERROR_MESSAGE = "errorMessage";
    public static final String REDIRECT_LOGIN = "redirect:/login";


    @Autowired
    private IUserService userService;

    @Autowired
    private PasswordMatchValidator passwordMatchValidator;

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

            // Custom password match validation
            passwordMatchValidator.validate(userDTO, result);

            // Check for existing email and phone
            if (userService.isEmailTaken(userDTO.getEmail())) {
                result.rejectValue("email", "email.exists", "Email already registered");
            }
            if (userService.isPhoneNumberTaken(userDTO.getPhoneNumber())) {
                result.rejectValue("phoneNumber", "phone.exists", "Phone number already registered");
            }

            if (result.hasErrors()) {
                log.warn("Validation errors occurred: {}", result.getAllErrors());
                return REGISTRATION;
            }

            User savedUser = userService.registerUser(userDTO);
            log.info("Successfully registered user with email: {}", savedUser.getEmail());
            model.addAttribute(USER_FULL_NAME, session.getAttribute(USER_FULL_NAME));
            session.setAttribute(USER_ID,savedUser.getId());
            session.setAttribute("userType",savedUser.getUserType());
            session.setAttribute("loggedInUser", savedUser);
            return "redirect:/dashboard";

        } catch (Exception e) {
            //log.error("Error in registration: ", e);
            model.addAttribute(ERROR_MESSAGE, "An error occurred: " + e.getMessage());
            return REGISTRATION;
        }
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
                            Model model,
                            RedirectAttributes redirectAttributes) {
        try {
            log.info("Login attempt for email: {}", email);

            User user = userService.authenticateUser(email, password);

            if (user != null) {
                // Store user information in session
                session.setAttribute(USER_ID, user.getId());
                session.setAttribute("userEmail", user.getEmail());
                session.setAttribute(USER_FULL_NAME, user.getFullName());
                session.setAttribute("loggedInUser", user);
                session.setAttribute("userType", user.getUserType());
                session.setAttribute("userIsThere",true);

                log.info("User successfully logged in: {}", email);
                model.addAttribute(USER_FULL_NAME, session.getAttribute(USER_FULL_NAME));
                return "dashboard";  // Redirect to dashboard after successful login
            } else {
                log.warn("Failed login attempt for email: {}", email);
                redirectAttributes.addFlashAttribute("error", "Invalid email or password");
                return "redirect:/login?error";
            }
        } catch (Exception e) {
            log.error("Error during login: ", e);
            redirectAttributes.addFlashAttribute("error", "An error occurred during login");
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

        if (result.hasErrors()) {
            return "EditProfile";
        }

        try {
            userService.updateProfile(userId, profileDTO);
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Profile updated successfully!");
            return "redirect:/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Error updating profile: " + e.getMessage());
            return "redirect:/profile/edit";
        }
    }

    @PostMapping("/profile/deactivate")
    public String deactivateAccount(HttpSession session, RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute(USER_ID);
        if (userId == null) {
            return REDIRECT_LOGIN;
        }
        try {
            userService.deactivateAccount(userId);
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


}