package com.quick_park_assist.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
//@RequestMapping("/success/")
public class SuccessController {

    @GetMapping("/success/")
    public String showSuccessPage(@RequestParam("message") String message, Model model) {
        // Add the message to the model for display on the success page
       // System.out.println("Working inside Saving form controller");
        model.addAttribute("message", message);
        return "success";
    }
}
