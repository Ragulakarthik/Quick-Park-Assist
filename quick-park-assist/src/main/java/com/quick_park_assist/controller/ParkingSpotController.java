package com.quick_park_assist.controller;

import com.quick_park_assist.entity.ParkingSpot;
import com.quick_park_assist.entity.User;
import com.quick_park_assist.repository.ParkingSpotRepository;
import com.quick_park_assist.repository.UserRepository;
import com.quick_park_assist.service.IParkingSpotPriceService;
import com.quick_park_assist.service.IUpdateParkingSpotService;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/smart-spots/")
public class ParkingSpotController {

    public static final String USER_ID = "userId";
    public static final String USER_TYPE = "userType";
    public static final String REDIRECT_LOGIN = "redirect:/login";
    public static final String SPOT_OWNER = "SPOT_OWNER";
    public static final String REDIRECT_SMART_SPOTS_SEARCH = "redirect:/smart-spots/search";
    public static final String ERROR_MESSAGE = "errorMessage";
    public static final String SUCCESS_MESSAGE = "successMessage";
    public static final String EV_SPOT = "EV_SPOT";
    public static final String AVAILABLE = "Available";
    public static final String REDIRECT_SMART_SPOTS_UPDATE_SPOT = "redirect:/smart-spots/update-spot";
    public static final String SEARCH_PARKING_SPOT = "SearchParkingSpot";
    public static final String PARKING_SPOTS = "parkingSpots";
    @Autowired
    private ParkingSpotRepository parkingSpotRepository;
    @Autowired
    private IParkingSpotPriceService parkingSpotService;
    @Autowired
    private IUpdateParkingSpotService updateParkingSpotService;
    @Autowired
    private UserRepository userRepository;

    private final Logger log = LogManager.getLogger(ParkingSpotController.class);
    public ParkingSpotController(ParkingSpotRepository parkingSpotRepository) {
        this.parkingSpotRepository = parkingSpotRepository;
    }

    @GetMapping("/add-spot")
    public String showAddSpotForm(HttpSession session, Model model) {
        // Retrieve the logged-in user from the session
        Long loggedInUser = (Long) session.getAttribute(USER_ID);
        String userType =  (String) session.getAttribute(USER_TYPE);

        if (loggedInUser == null) {
            return REDIRECT_LOGIN; // Redirect to login if user is not in session
        }
        if(userType.equalsIgnoreCase(SPOT_OWNER)){
            model.addAttribute("parkingSpot", new ParkingSpot());
            return "AddParkingSpot"; // Show the add spot form page
        }
        return REDIRECT_SMART_SPOTS_SEARCH;
    }

    @PostMapping("/add-spot/")
    public String addSpot(HttpSession session, @ModelAttribute("parkingSpot") ParkingSpot parkingSpot, Model model,RedirectAttributes redirectAttributes) {
        // Get the logged-in userId from the session
        Long userId = (Long) session.getAttribute(USER_ID);

        if (userId == null) {
            return REDIRECT_LOGIN; // Redirect to login if the user is not logged in
        }

        // Fetch the User entity from the database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Before adding the Spot we need to check whether it's unique or not.
        if(parkingSpotRepository.existsParkingSpotBySpotLocationIgnoreCaseAndLocationIgnoreCase(parkingSpot.getSpotLocation(),parkingSpot.getLocation())){
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE,"The Spot is Already Owned, Please Choose another Spot Location.");
            return "redirect:/smart-spots/add-spot";
        }
        // Set the user to the bookingSpot
        parkingSpot.setUser(user);

        parkingSpotRepository.save(parkingSpot);// Save the parking spot to the database
        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Your new Parking Spot is now Added!");
        return "redirect:/smart-spots/add-spot"; // Redirect after saving
    }

    @GetMapping("/searching")
    @ResponseBody
    public List<ParkingSpot> searchLocations(@RequestParam(value = "query", required = false) String query) {
        return query != null && !query.isEmpty()
                ? parkingSpotRepository.findByAvailabilityIgnoreCaseAndSpotLocationContainingIgnoreCaseOrLocationContainingIgnoreCaseAndSpotTypeNot(AVAILABLE,query,query, EV_SPOT)
                : new ArrayList<>();
    }


    @GetMapping("/search")
    public String showSearchParkingSpotsForm(
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "availability", required = false) String availability,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        // Retrieve the logged-in user from the session
        Long loggedInUser = (Long) session.getAttribute(USER_ID);

        if (loggedInUser == null) {
            return REDIRECT_LOGIN; // Redirect to login if user is not in session
        }

        if (location == null || location.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Please enter a location to search.");
            return SEARCH_PARKING_SPOT;
        }
        log.info("Searching for location:  {}, availability: {}", location , availability);
        List<ParkingSpot> parkingSpots;
        try {
            if ("all".equalsIgnoreCase(availability)) {
                parkingSpots = parkingSpotRepository.findByLocationContainingIgnoreCaseAndSpotTypeNot(location, EV_SPOT);
            } else {
                parkingSpots = parkingSpotRepository.findByLocationContainingIgnoreCaseAndAvailabilityIgnoreCaseAndSpotTypeNot(
                        location, AVAILABLE.equalsIgnoreCase(availability) ? AVAILABLE : "Unavailable", EV_SPOT);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "An error occurred while searching for parking spots.");
            return SEARCH_PARKING_SPOT;
        }

        if (parkingSpots.isEmpty()) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "No parking spots found for the given location and availability.");
        } else {
            model.addAttribute(PARKING_SPOTS, parkingSpots);
        }

        model.addAttribute("location", location);
        model.addAttribute("availability", availability);

        return SEARCH_PARKING_SPOT;
    }

    @GetMapping("/update-spot")
    public String getParkingSpots(Model model, HttpSession session) {
        // Retrieve the logged-in user from the session
        Long loggedInUser = (Long) session.getAttribute(USER_ID);
        String userType = (String) session.getAttribute(USER_TYPE);

        if (loggedInUser == null) {
            return REDIRECT_LOGIN; // Redirect to login if user is not in session
        }
        if(userType.equalsIgnoreCase(SPOT_OWNER)) {
            // Fetch parking spots for the logged-in user
            List<ParkingSpot> parkingSpots = updateParkingSpotService.getParkingSpotsForLoggedInUser(loggedInUser);

            // Add parking spots to the model
            model.addAttribute(PARKING_SPOTS, parkingSpots);
            return "EditParkingSpot"; // Name of the Thymeleaf template
        }
        return REDIRECT_SMART_SPOTS_SEARCH;
    }

    @PostMapping("/updateSpot")
    public String updateParkingSpot(
            @RequestParam(value = "id", required = true) Long spotId,
            @RequestParam(value = "availability", required = true) String availability,
            @RequestParam(value = "pricePerHour", required = true) Double pricePerHour,
            @RequestParam(value = "spotType", required = true) String spotType,
            @RequestParam(value = "additionalInstructions", required = true) String additionalInstructions,
            RedirectAttributes redirectAttributes,
            Model model) {
        try {
            boolean isUpdated = updateParkingSpotService.updateParkingSpot(spotId, availability, pricePerHour, spotType, additionalInstructions);
            if (isUpdated) {
                redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Parking spot updated successfully!");
            } else {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Failed to update the parking spot, Try Again.");
            }
        } catch (Exception e) {
            log.error("ERROR: {}",e.getMessage());
        }
        return REDIRECT_SMART_SPOTS_UPDATE_SPOT;
    }

    // Show the remove spot page
    @GetMapping("/remove")
    public String showRemoveParkingSpot( Model model, HttpSession session,RedirectAttributes redirectAttributes) {
        Long loggedInUser = (Long) session.getAttribute(USER_ID);
        String userType = (String) session.getAttribute(USER_TYPE);
        if (loggedInUser == null) {
            return REDIRECT_LOGIN; // Redirect to log-in if user is not in session
        }
        if(userType.equalsIgnoreCase(SPOT_OWNER)) {
            // Fetch updated parking spots for the logged-in user
            List<ParkingSpot> parkingSpots = updateParkingSpotService.getParkingSpotsForLoggedInUser(loggedInUser);
            if (parkingSpots.isEmpty()) {
                redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Parking spot not found.");
            } else {
                model.addAttribute(PARKING_SPOTS, parkingSpots);
            }
            return "RemoveParkingSpot"; // Show the updated remove spot page
        }
        return REDIRECT_SMART_SPOTS_SEARCH;
    }

    @PostMapping("/remove")
    public String removeParkingSpot(@RequestParam("id") Long id, Model model, HttpSession session,RedirectAttributes redirectAttributes) {
        Long loggedInUser = (Long) session.getAttribute(USER_ID);

        if (loggedInUser == null) {
            return REDIRECT_LOGIN; // Redirect to log-in if user is not in session
        }

        Optional<ParkingSpot> parkingSpot = parkingSpotRepository.findById(id);
        if (parkingSpot.isPresent()) {
            parkingSpotRepository.delete(parkingSpot.get()); // Remove the parking spot from the database
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Parking spot removed successfully.");
        } else {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Parking spot not found.");
        }

        // Fetch updated parking spots for the logged-in user
        List<ParkingSpot> parkingSpots = updateParkingSpotService.getParkingSpotsForLoggedInUser(loggedInUser);
        model.addAttribute(PARKING_SPOTS, parkingSpots);

        return "redirect:/smart-spots/remove"; // Show the updated remove spot page
    }
}