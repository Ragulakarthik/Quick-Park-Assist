package com.quick_park_assist.controller;

import com.quick_park_assist.entity.BookingSpot;
import com.quick_park_assist.service.IBookingHistoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/bookingHistory")
public class BookingHistoryController {
    @Autowired
    IBookingHistoryService IBookinghistoryservice;

    @GetMapping("/")
    public  String showHistoryForm(HttpSession session , Model model){
        Long loggedInUser = (Long) session.getAttribute("userId");
        if (loggedInUser == null) {
            return "redirect:/login"; // Redirect to login if user is not in session
        }
        List<BookingSpot> bookings = IBookinghistoryservice.getBookingsByuserID(loggedInUser);

        model.addAttribute("bookings", bookings);
        model.addAttribute("cancelSpot", new BookingSpot());
        return "BookingHistory";
    }
    @GetMapping("/booking-history")
    public String handleGetRequest() {
        return "redirect:/bookingHistory/";
    }
}
