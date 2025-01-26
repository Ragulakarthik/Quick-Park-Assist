package com.quick_park_assist.controller;

import com.quick_park_assist.entity.BookingSpot;

import com.quick_park_assist.service.IModifySpotService;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/modifySpot")
public class ModifySpotController {
    public static final String REDIRECT_MODIFY_SPOT = "redirect:/modifySpot/";
    public static final String ERROR_MESSAGE = "errorMessage";
    @Autowired
    private IModifySpotService IModifyspotservice;
    private String mobileNumber;
    private Model model;


    @GetMapping("/")
    @Transactional
    public String fetchConfirmedBookings(
            HttpSession session, Model model) {

        Long loggedInUser = (Long) session.getAttribute("userId");

        if (loggedInUser == null) {
            return "redirect:/login"; // Redirect to login if user is not logged in
        }
        this.model = model;
        List<BookingSpot> confirmedBookings = IModifyspotservice.getConfirmedBookings(loggedInUser);
        model.addAttribute("bookings", confirmedBookings);
        model.addAttribute("mobileNumber", mobileNumber);
        return "ModifyBooking";
    }


    @GetMapping("/update-spot")
    public String handleGetRequest() {
        return REDIRECT_MODIFY_SPOT;
    }

    @PostMapping("/update-spot")
    @Transactional
    public String updateSpotDetails(
            @RequestParam(value = "bookingId", required = true) Long bookingId,
            @RequestParam(value = "spotId",required = true) Long spotID,
            @RequestParam(value = "startTime", required = true) String startTimeStr,
            @RequestParam(value = "duration", required = true) Double duration,
            RedirectAttributes redirectAttributes) {

        try {
            // Parse startTime from the ISO format
            SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

            Date startTime = dateTimeFormatter.parse(startTimeStr);
            Date now = new Date();
            // Validate startTime and duration
            if (startTime.before(now)) {
                redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Please select a future date and time.");
                return REDIRECT_MODIFY_SPOT;
            }

            if (duration == null || duration <= 0) {
                redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Please select a valid duration.");
                return REDIRECT_MODIFY_SPOT;
            }

            // Call service to update spot details
            boolean isUpdated = IModifyspotservice.updateSpotDetails(bookingId, startTime, duration,spotID);

            if (isUpdated) {
                redirectAttributes.addFlashAttribute("successMessage", "Booking updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Update failed. Booking ID not found.");
            }

        } catch (DateTimeParseException e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Invalid date format. Please use the correct format (yyyy-MM-dd'T'HH:mm).");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Invalid date format. Please use the correct format.");
        }
        catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Invalid date format. Please use the correct format.Try Again");
        }

        return REDIRECT_MODIFY_SPOT;
    }

}
