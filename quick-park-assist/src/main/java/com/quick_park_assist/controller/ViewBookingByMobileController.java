package com.quick_park_assist.controller;

import java.util.List;

import com.quick_park_assist.service.IViewBookingByMobileService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.quick_park_assist.entity.BookingSpot;

@Controller
@RequestMapping("/viewBookingByMobile")
public class ViewBookingByMobileController {

    @Autowired
    private IViewBookingByMobileService viewByMobileNumberService;

    @GetMapping("/")
    public String showBookingForm(HttpSession session,Model model) {
        Long loggedInUser = (Long) session.getAttribute("userId");

        if (loggedInUser == null) {
            return "redirect:/login"; // Redirect to login if user is not in session
        }
        model.addAttribute("bookingSpot", new BookingSpot());
        List<BookingSpot> bookings = viewByMobileNumberService.getConfirmedBookingsByUserID(loggedInUser);
        model.addAttribute("bookings", bookings);
        return "ViewBookingByMobile";
    }
}
