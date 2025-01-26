package com.quick_park_assist.controller;

import com.quick_park_assist.entity.BookingSpot;
import com.quick_park_assist.service.ICancelSpotService;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Controller
@RequestMapping("/cancelSpot")
public class CancelSpotController {

    // creating a logger
    Logger logger
            = LoggerFactory.getLogger(CancelSpotController.class);
    @Autowired
    ICancelSpotService cancelSpotService;

    @GetMapping("/")
    public String showCancelForm(HttpSession session, Model model) {
        Long loggedInUser = (Long) session.getAttribute("userId");
        if (loggedInUser == null) {
            return "redirect:/login"; // Redirect to login if user is not in session
        }
        List<BookingSpot> confirmedBookings = cancelSpotService.getConfirmedBookingsByUserID(loggedInUser);
        if (confirmedBookings == null || confirmedBookings.isEmpty()) {

            logger.info("No confirmed bookings found for user ID:"+loggedInUser);
        } else {
            for (BookingSpot booking : confirmedBookings) {
                System.out.println("Booking ID: " + booking.getBookingId());
            }
        }
        model.addAttribute("bookings", confirmedBookings);
        model.addAttribute("cancelSpot", new BookingSpot());
        return "CancelBooking";
    }
/*    @PostMapping("/fetchConfirmedBookings")
    public String fetchConfirmedBookings(@RequestParam("mobileNumber") String mobileNumber, Model model) {
        List<BookingSpot> confirmedBookings = cancelSpotService.getConfirmedBookingsByUserID();
        model.addAttribute("bookings", confirmedBookings);
        model.addAttribute("mobileNumber", mobileNumber);
        return "CancelBooking";
    }*/
    @PostMapping("/cancelSelectedBooking")
    @Transactional
    public String cancelSelectedBooking(@RequestParam Long bookingId, RedirectAttributes redirectAttributes) {
        if (bookingId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid Booking ID.");
            return "redirect:/cancelSpot/";
        }
        boolean isCancelled = cancelSpotService.cancelBooking(bookingId);
        if (isCancelled) {
            redirectAttributes.addFlashAttribute("successMessage", "Booking successfully Cancelled!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Cancellation failed.");
        }
        return "redirect:/cancelSpot/";
    }
}
