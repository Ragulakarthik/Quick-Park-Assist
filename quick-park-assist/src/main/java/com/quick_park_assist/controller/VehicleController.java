package com.quick_park_assist.controller;

import com.quick_park_assist.dto.VehicleDTO;
import com.quick_park_assist.entity.Vehicle;
import com.quick_park_assist.repository.VehicleRepository;
import com.quick_park_assist.service.IVehicleService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/vehicles")
public class VehicleController {

    public static final String REDIRECT_LOGIN = "redirect:/login";
    public static final String USER_ID = "userId";
    public static final String REDIRECT_DASHBOARD = "redirect:/dashboard";
    public static final String ERROR_MESSAGE = "errorMessage";
    public static final String SUCCESS_MESSAGE = "successMessage";
    public static final String VEHICLE = "vehicle";
    public static final String ADD_VEHICLE = "AddVehicle";
    @Autowired
    private IVehicleService vehicleService;
    @Autowired
    private VehicleRepository vehicleRepository;



    @GetMapping("/editVehicle")
    public String listVehicles(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute(USER_ID);
        if (userId == null) {
            return REDIRECT_LOGIN;
        }
        List<Vehicle> vehicles = vehicleService.getVehiclesByUserId(userId);

        model.addAttribute("vehicles", vehicles);
        return "EditVehicle";
    }

    @GetMapping("/add")
    public String showAddVehicleForm(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute(USER_ID);
        if (userId == null) {
            return REDIRECT_LOGIN;
        }
        model.addAttribute(VEHICLE, new VehicleDTO());
        return ADD_VEHICLE;
    }

    @PostMapping("/new-vehicle")
    public String addVehicle(@Valid @ModelAttribute(VEHICLE) VehicleDTO vehicleDTO,
                             BindingResult result,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return ADD_VEHICLE;
        }

        Long userId = (Long) session.getAttribute(USER_ID);
        if (userId == null) {
            return REDIRECT_LOGIN;
        }
        String vehicleNum = vehicleDTO.getVehicleNumber();
        if(vehicleRepository.existsVehicleByVehicleNumber(vehicleNum)){
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE,"Vehicle Already Registered. Enter new Vehicle Number!");
            return "redirect:/vehicles/add";
        }
        try {
            vehicleService.addVehicle(userId, vehicleDTO);
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Vehicle added successfully!");
            return REDIRECT_DASHBOARD;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Error adding vehicle: " + e.getMessage());
            return ADD_VEHICLE;
        }
    }

    @GetMapping("/{id}")
    public String viewVehicle(@PathVariable("id") Long vehicleId,
                              HttpSession session,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute(USER_ID);
        if (userId == null) {
            return REDIRECT_LOGIN;
        }

        try {
            Vehicle vehicle = vehicleService.getVehicleByIdAndUserId(vehicleId, userId);
            model.addAttribute(VEHICLE, vehicle);
            return "ListVehicle";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, e.getMessage());
            return REDIRECT_DASHBOARD;
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long vehicleId,
                               HttpSession session,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute(USER_ID);
        if (userId == null) {
            return REDIRECT_LOGIN;
        }

        try {
            Vehicle vehicle = vehicleService.getVehicleByIdAndUserId(vehicleId, userId);
            VehicleDTO vehicleDTO = new VehicleDTO();
            vehicleDTO.setVehicleNumber(vehicle.getVehicleNumber());
            vehicleDTO.setVehicleType(vehicle.getVehicleType());
            vehicleDTO.setManufacturer(vehicle.getManufacturer());
            vehicleDTO.setModel(vehicle.getModel());
            vehicleDTO.setColor(vehicle.getColor());

            model.addAttribute("vehicleId", vehicleId);
            model.addAttribute(VEHICLE, vehicleDTO);
            return "/";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, e.getMessage());
            return "redirect:/";
        }
    }
    // for Updating the Vehicle Information
    @PostMapping("/update/{id}")
    public String updateVehicle(@PathVariable("id") Long vehicleId,
                                @Valid @ModelAttribute(VEHICLE) VehicleDTO vehicleDTO,
                                BindingResult result,
                                HttpSession session,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute(USER_ID);
        if (userId == null) {
            return REDIRECT_LOGIN;
        }

        if (result.hasErrors()) {
            model.addAttribute("vehicleId", vehicleId);
            return "vehicles/edit";
        }

        try {
            vehicleService.updateVehicle(vehicleId, userId, vehicleDTO);
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Vehicle updated successfully!");
            return REDIRECT_DASHBOARD;
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Error updating vehicle: " + e.getMessage());
            return "redirect:/editVehicle";
        }
    }
    // for deleting the Vehicle
    @PostMapping("/delete/{id}")
    public String deleteVehicle(@PathVariable("id") Long vehicleId,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute(USER_ID);
        if (userId == null) {
            return REDIRECT_LOGIN;
        }

        try {
            vehicleService.deleteVehicle(vehicleId, userId);
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Vehicle deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Error deleting vehicle: " + e.getMessage());
        }

        return REDIRECT_DASHBOARD;
    }
    // Helper method to check if user is spot owner
    private boolean isSpotOwner(HttpSession session) {
        return "SPOT_OWNER".equals(session.getAttribute("userType"));
    }

    // Show search form for spot owners
    @GetMapping("/search")
    public String showSearchForm(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute(USER_ID);
        if (userId == null) {
            return REDIRECT_LOGIN;
        }

        // Check if user is a spot owner
        if (isSpotOwner(session)) {
            return REDIRECT_DASHBOARD;
        }

        return "SearchVehicle";
    }

    // Handle vehicle search
    @PostMapping("/search")
    public String searchVehicle(@RequestParam("vehicleNumber") String vehicleNumber,
                                HttpSession session,
                                Model model) {
        Long userId = (Long) session.getAttribute(USER_ID);
        if (userId == null) {
            return REDIRECT_LOGIN;
        }

        // Check if user is a spot owner
        if (isSpotOwner(session)) {
            return REDIRECT_DASHBOARD;
        }

        try {
           Optional<Vehicle> vehicle = vehicleRepository.findByVehicleNumber(vehicleNumber);
            if (vehicle.isEmpty()) {
                model.addAttribute(ERROR_MESSAGE, "No vehicle found with this number");
            }
            else{
                model.addAttribute(VEHICLE, vehicle.get());
            }
        } catch (Exception e) {
            model.addAttribute(ERROR_MESSAGE, "Error searching vehicle: " + e.getMessage());
        }

        return "SearchVehicle";
    }
}