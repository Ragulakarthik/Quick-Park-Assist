package com.quick_park_assist.controller;

import com.quick_park_assist.entity.User;

import com.quick_park_assist.repository.UserRepository;
import com.quick_park_assist.repository.VehicleRepository;
import com.quick_park_assist.service.IReservationService;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.quick_park_assist.entity.Reservation;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;
@Controller
@RequestMapping("/ev-charging")
public class ReservationController {


    public static final String USER_ID = "userId";
    public static final String RESERVATIONS = "reservations";
    public static final String ERROR_MESSAGE = "errorMessage";
    public static final String SUCCESS_MESSAGE = "successMessage";
    @Autowired
    private IReservationService reservationService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VehicleRepository vehicleRepository;

    private static final String REDIRECT_TO_LOGIN = "redirect:/login";
    private static final String REDIRECT_EV_CHARGING_ADD = "redirect:/ev-charging/add";

    @GetMapping("/list")
    public String listReservations(HttpSession session,Model model) {
        Long loggedInUser = (Long) session.getAttribute(USER_ID);
        if(loggedInUser == null){
            return REDIRECT_TO_LOGIN;
        }
        List<Reservation> reservations = reservationService.getReservationsByUserId(loggedInUser);
        
        // Format the reservation time
        model.addAttribute(RESERVATIONS, reservations);
        return "ViewReservations";
    }

    // Show form for adding a new reservation
    @GetMapping("/add")
    public String addReservationForm(HttpSession session, Model model,RedirectAttributes redirectAttributes) {
        Long loggedInUser = (Long) session.getAttribute(USER_ID);
        if (loggedInUser == null) {
            return REDIRECT_TO_LOGIN;
        }
        if (vehicleRepository.existsElectricVehicleByUserId(loggedInUser)) {
            model.addAttribute("reservation", new Reservation());
            return "addReservation";  // Thymeleaf template 'addReservation.html'
        }
        else{
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE,"You currently Don't have an EV to Reserve a spot");
            return "redirect:/vehicles/add";
        }
    }

    // Process the form for adding a new reservation
    @PostMapping("/add-reservation")
    public String addReservation(HttpSession session, Reservation reservation, RedirectAttributes redirectAttributes) {
        // Get the logged-in userId from the session
        Long userId = (Long) session.getAttribute(USER_ID);
        if (userId == null) {
            return REDIRECT_TO_LOGIN; // Redirect to login if the user is not logged in
        }

        // Parse startTime from the ISO format
        Date present = new Date();
        if (reservation.getReservationTime().before(present)) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Please Choose Correct Date and time");
            return REDIRECT_EV_CHARGING_ADD;
        }

        // Getting the Vehicle-Number
        String vehicleNum = reservation.getVehicleNumber();

        // Check if the vehicle number belongs to the user and is an EV
        if (vehicleRepository.existsVehicleByVehicleNumberAndUserIdAndEvTrue(vehicleNum, userId)) {
            // Check for overlapping reservations
            if (reservationService.isTimeSlotAvailable(reservation.getReservationTime(), vehicleNum)) {
                // Fetch the User entity from the database
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                // Set the user to the bookingSpot
                reservation.setUser(user);
                reservation.setStatus("CONFIRMED");
                reservationService.addReservation(reservation);
                redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Reservation is Successful!");
                return REDIRECT_EV_CHARGING_ADD;
            } else {
                redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "The selected time slot is already booked.");
                return REDIRECT_EV_CHARGING_ADD;
            }
        }
        redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "The Vehicle Number is not Registered or not an EV");
        return REDIRECT_EV_CHARGING_ADD; // After adding, redirect to the reservation list
    }


    @GetMapping("/edit")
    public String editForm(HttpSession session, Model model) {
        Long loggedInUser = (Long) session.getAttribute(USER_ID);
        if(loggedInUser == null){
            return REDIRECT_TO_LOGIN;
        }
        List<Reservation> reservations = reservationService.getReservationsByUserId(loggedInUser);

        // Format the reservation time

        model.addAttribute(RESERVATIONS, reservations);
        model.addAttribute("vehicleNumber",""); // empty string to capture the vehicle number
        return "EditReservation"; // Template to input vehicle number
    }
    @PostMapping("/update-reservation")
    @Transactional
    public String updateSpotDetails(
            @RequestParam(value = "id", required = true) Long id,
            @RequestParam(value = "startTime", required = true) String startTimeStr,
            @RequestParam(value = "vehicleNumber", required = true) String vehicleNumber,
            RedirectAttributes redirectAttributes,
            Model model) {
        try {
            // Define a formatter (this format must match your input string)
            SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            Date present = new Date();
            Date startTime = dateTimeFormatter.parse(startTimeStr);
            if(startTime.before(present)) {
                redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Invalid date format. Please use the correct format.");
                return "redirect:/ev-charging/edit";
            }
            boolean isUpdated = reservationService.updateSpotDetails(id, startTime, vehicleNumber);

            if (isUpdated) {
                redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Booking updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Update failed. Booking ID not found.");
            }
        }catch(java.text.ParseException e){
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE,"Exception Invalid date format. Please use the correct format.");
        }
        return "redirect:/ev-charging/edit";
    }

    // Step 3: Handle the form submission for updating the reservation
    // Show the delete reservation form
    @GetMapping("/delete-form")
    public String showDeleteForm(HttpSession session, Model model) {
        Long loggedInUser = (Long) session.getAttribute(USER_ID);
        if(loggedInUser == null){
            return REDIRECT_TO_LOGIN;
        }
        List<Reservation> reservations = reservationService.getReservationsByUserId(loggedInUser);

        model.addAttribute(RESERVATIONS, reservations);
        return "CancelReservation";
    }

    // Handle the form submission to delete the reservation



    @PostMapping("/delete/{id}")
    public String deleteReservation(@RequestParam("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        // Call service to delete the reservation based on the ID
        boolean isDeleted = reservationService.deleteReservationById(id);
        if (isDeleted) {
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Reservation Successfully Cancelled");
        } else {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Reservation Couldn't be Cancelled");
        }

        return "redirect:/ev-charging/delete-form"; // Redirect to the delete-form page
    }


}
