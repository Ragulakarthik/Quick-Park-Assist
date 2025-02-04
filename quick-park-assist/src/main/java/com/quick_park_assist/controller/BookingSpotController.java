package com.quick_park_assist.controller;

import com.quick_park_assist.entity.ParkingSpot;
import com.quick_park_assist.entity.User;

import com.quick_park_assist.repository.BookingSpotRepository;
import com.quick_park_assist.repository.ParkingSpotRepository;
import com.quick_park_assist.repository.UserRepository;
import com.quick_park_assist.service.IParkingSpotService;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.quick_park_assist.entity.BookingSpot;
import com.quick_park_assist.service.IBookingSpotService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/bookingSpot/")
public class BookingSpotController {

    public static final String LOGGED_IN_USER = "loggedInUser";
    public static final String REDIRECT_BOOKING_SPOT = "redirect:/bookingSpot/";
    public static final String ERROR_MESSAGE = "errorMessage";
    @Autowired
    private IBookingSpotService IbookingSpotService;
    @Autowired
    private IParkingSpotService parkingSpotService;
    @Autowired
    private BookingSpotRepository bookingSpotRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ParkingSpotRepository parkingSpotRepository;
    public Logger logger = LogManager.getLogger(BookingSpotController.class);
    @GetMapping("/")
    public String showBookingSpotForm(
            @RequestParam(value = "query", required = false) String searchQuery, // Accept user input
            HttpSession session ,
            Model model) { // Retrieve the user from the session
        Long userId = (Long) session.getAttribute("userId");
        User loggedInUser = (User) session.getAttribute(LOGGED_IN_USER);
        if (userId == null) {
            return "redirect:/login"; // Redirect to login if user is not logged in
        }
        Optional<User> currentUser = userRepository.findByEmail(loggedInUser.getEmail());
        if (currentUser.isPresent()){
            User user = currentUser.get();
            model.addAttribute(LOGGED_IN_USER, user);
        }
        else{
            model.addAttribute(LOGGED_IN_USER, loggedInUser); // Fallback if not found in DB
        }

        model.addAttribute("bookingSpot", new BookingSpot());
        // Fetch available parking spots and add to model
        return "myBookingSpot";
    }
    @GetMapping("/searching")
    @ResponseBody
    public List<Map<String, String>> searchParkingSpots(
            @RequestParam("query") String query,
            @RequestParam(value = "query", required = false) String searchQuery, // Accept user input
            HttpSession session ,
            Model model) {
        User loggedInUser = (User) session.getAttribute(LOGGED_IN_USER);
        Optional<User> currentUser = userRepository.findByEmail(loggedInUser.getEmail());
        if (currentUser.isPresent()){
            User user = currentUser.get();
            model.addAttribute(LOGGED_IN_USER, user);
        }
        else{
            model.addAttribute(LOGGED_IN_USER, loggedInUser); // Fallback if not found in DB
        }
        // Fetch spots based on query (location-based search)
        List<ParkingSpot> parkingSpots = parkingSpotService.getAllAvailableSpots(query);
        // Format the response as JSON with spot ID and location
        List<Map<String, String>> formattedSpots = new ArrayList<>();
        for (ParkingSpot spot : parkingSpots) {
            Map<String, String> spotData = new HashMap<>();
            spotData.put("id", String.valueOf(spot.getId())); // Spot ID
            spotData.put("location", spot.getLocation());    // Location
            formattedSpots.add(spotData);
        }

        return formattedSpots; // Return formatted JSON response

    }

    @GetMapping("/redirect-to-booking")
    public String redirectToBookingPage() {
        return REDIRECT_BOOKING_SPOT;
    }

    @Transactional
    @PostMapping("/book-spot/")
    public String submitBookingSpotForm(
            @RequestParam("spotId") Long spotId,
            @RequestParam("spotLocation") String spotLocation,
            RedirectAttributes redirectAttributes,
            @ModelAttribute("bookingSpot") BookingSpot bookingSpot,
            HttpSession session) {
        // Get the logged-in userId from the session
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login"; // Redirect to login if the user is not logged in
        }

        try {
            // Ensure startTime is after the current time
            Date now = new Date();
            if (bookingSpot.getStartTime().before(now)) {
                redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Start time must be in the future.");
                return REDIRECT_BOOKING_SPOT;
            }

            // Validate the duration
            if (bookingSpot.getDuration() == null || bookingSpot.getDuration() <= 0) {
                redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Please provide a valid duration.");
                return REDIRECT_BOOKING_SPOT;
            }
            // Now here we will check If we already have a booking in the same spot
            // here we'll check if the spot had previously booked on the sameTime

            if(IbookingSpotService.checkIfPreviouslyBooked(userId,spotId,bookingSpot.getStartTime())) {
                // Calculate the endTime by adding duration to the startTime
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(bookingSpot.getStartTime());
                int durationMinutes = (int) (bookingSpot.getDuration() * 60);
                calendar.add(Calendar.MINUTE, durationMinutes);
                Date endTime = calendar.getTime(); // Convert duration to minutes
                bookingSpot.setEndTime(endTime);

                // Fetch the User entity from the database
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                // Set the user and additional details to the bookingSpot
                bookingSpot.setUser(user);
                bookingSpot.setMobileNumber(user.getPhoneNumber());
                Optional<ParkingSpot> parkingSpotOptional = parkingSpotRepository.findById(spotId);

                if (parkingSpotOptional.isEmpty()) {
                    redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Parking spot not found.");
                    return REDIRECT_BOOKING_SPOT;
                }
                ParkingSpot parkingSpot = parkingSpotOptional.get();
                bookingSpot.setSpotId(parkingSpot);
                bookingSpot.setSpotLocation(spotLocation);
                // Save the bookingSpot
                bookingSpotRepository.save(bookingSpot);

                // Generate a success message
                redirectAttributes.addFlashAttribute("successMessage", "Your booking is successful!");
            }
            else{
                redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "It is already booked for the start time you have chosen. Please select another time or another spotLocation.");
            }
        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "An error occurred during booking.");
        }
        return REDIRECT_BOOKING_SPOT;
    }

    @GetMapping("/get-price-per-hour")
    @ResponseBody
    public Map<String, Object> getPricePerHour(@RequestParam("spotId") Long spotId, RedirectAttributes redirectAttributes) {
        Map<String, Object> response = new HashMap<>();

        if (spotId == null) {
            response.put("error","Response should be null when parking spot does not exist");
            throw new IllegalArgumentException("Spot ID cannot be null.");
        }

        Optional<ParkingSpot> parkingSpot = parkingSpotRepository.findById(spotId);

        if (parkingSpot.isPresent()) {
            response.put("pricePerHour", parkingSpot.get().getPricePerHour());
        } else {
            response.put("error", "Parking spot not found for the given ID.");
            logger.warn("Parking spot with ID {} not found.", spotId);
        }

        return response;
    }





}