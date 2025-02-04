package com.quick_park_assist.controller;

import com.quick_park_assist.entity.BookingSpot;
import com.quick_park_assist.service.IViewBookingBySpotService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/viewBookingBySpot")
public class ViewBookingBySpotController {

    @Autowired
    private IViewBookingBySpotService viewBySpotService;

    @GetMapping("/")
    public String showSpotForm(HttpSession session, Model model) {
        Long loggedInUser = (Long) session.getAttribute("userId");

        if (loggedInUser == null) {
            return "redirect:/login"; // Redirect to login if user is not in session
        }
        model.addAttribute("bookingSpot", new BookingSpot());

        return "ViewBookingBySpot";
    }

    @PostMapping("/viewbooking-spot")
    public String getBookingDetails(@RequestParam("spotLocation") String spotLocation,HttpSession session, Model model) {
        Long loggedInUser = (Long) session.getAttribute("userId");
        if(loggedInUser == null){
            return "redirect:/login";
        }
        List<BookingSpot> bookings = viewBySpotService.getBookingsBySpotLocation(loggedInUser,spotLocation);
        model.addAttribute("bookings", bookings);
        model.addAttribute("spotLocation", spotLocation);
        model.addAttribute("bookings", bookings);
        return "ViewBookingBySpot";
    }
}

