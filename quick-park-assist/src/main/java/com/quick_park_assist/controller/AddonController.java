package com.quick_park_assist.controller;

import com.quick_park_assist.entity.AddonService;
import com.quick_park_assist.entity.ServiceEntity;
import com.quick_park_assist.entity.User;
import com.quick_park_assist.repository.ServiceRepository;
import com.quick_park_assist.service.IAddonService;
import com.quick_park_assist.service.IModifyOwnerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.quick_park_assist.repository.UserRepository;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/addon")
public class AddonController {

    private static final String USER_ID = "userId";
    public static final String ADDONS = "addons";
    public static final String ADDON = "addon";
    public static final String REDIRECT_LOGIN = "redirect:/login";
    public static final String REDIRECT_ADDON_NEW = "redirect:/addon/new";
    public static final String ERROR_MESSAGE = "errorMessage";
    public static final String SUCCESS_MESSAGE = "successMessage";
    public static final String OWNER_SERVICES = "ownerServices";

    @Autowired
    private IAddonService addonServiceHandler;
    @Autowired
    private  ServiceRepository serviceRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private IModifyOwnerService modifyOwnerService;



    @GetMapping("/all")
    public String viewAllAddons(Model model,HttpSession session)
    {
        Long loggedInUser= (Long)session.getAttribute(USER_ID);
        if(loggedInUser==null)
        {
            return "login";
        }
        List<ServiceEntity> services = serviceRepository.findAll();
        model.addAttribute(ADDONS, services);
        model.addAttribute(ADDON, new AddonService());
        return "addon-services";
    }

    @GetMapping("/new")
    public String createAddonForm(HttpSession session,Model model) {
        Long loggedInUser = (Long)session.getAttribute(USER_ID);
        String userType = (String) session.getAttribute("userType");
        if(loggedInUser==null)
        {
            return REDIRECT_LOGIN;
        }
        // here we will display a page for vehicle Owner
        if(userType.equalsIgnoreCase("VEHICLE_OWNER")) {
            List<ServiceEntity> services = serviceRepository.findAll();
            // Fetch all services
            model.addAttribute("services", services);
            model.addAttribute(ADDON, new AddonService()); // Add a new AddonService object
            return "create-addon"; // Return the view name
        }
        // here we will display another page for SPOT Owner
        else {
            model.addAttribute("newService",new ServiceEntity());
            return "CreateService";
        }
    }
    @PostMapping("/create")
    public String createNewService(@ModelAttribute("newService") ServiceEntity serviceEntity, RedirectAttributes redirectAttributes, HttpSession session){
        Long userId = (Long) session.getAttribute(USER_ID);
        if(userId == null){
            return REDIRECT_LOGIN;
        }
        // Fetch the User entity from the database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Set the user to the bookingSpot
        if(serviceEntity.getPrice() < 0){
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Price should be above '0'");
            return REDIRECT_ADDON_NEW;
        }
        if(serviceRepository.existsServiceEntityByNameIgnoreCase(serviceEntity.getName())){
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE,"The Service is already created By you!");
            return REDIRECT_ADDON_NEW;
        }
        serviceEntity.setUser(user);
        serviceRepository.save(serviceEntity);
        // Generate a success message based on the action
        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Your Service is Successfully created!");
        return REDIRECT_ADDON_NEW;
    }

    @GetMapping("/modify-service")
    public String modifyService(HttpSession session, Model model){

        Long loggedInUser = (Long) session.getAttribute(USER_ID);

        if (loggedInUser == null) {
            return REDIRECT_LOGIN; // Redirect to login if user is not logged in
        }
        List<ServiceEntity> ownerServices = modifyOwnerService.getOwnerServices(loggedInUser);
        if(ownerServices.isEmpty()){
            model.addAttribute(ERROR_MESSAGE, "You currently do not have any services");
        }
        model.addAttribute(OWNER_SERVICES,ownerServices);
        return "EditOwnerService";
    }
    @PostMapping("/modifyService")
    public String modifyOwnerSerice(
            @RequestParam(value = "id", required=true)  Long id,
            @RequestParam(value = "name",required = true) String name,
            @RequestParam(value ="description",required = true) String description,
            @RequestParam(value ="price",required = true) Double price,
            RedirectAttributes redirectAttributes) {
        boolean isUpdated = modifyOwnerService.updateServiceDetails(id, name, description,price);
        if (isUpdated) {
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Booking updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Update failed. UserID not found.");
        }
        return "redirect:/addon/modify-service";
    }
    @PostMapping("/save")
    public String saveAddon(@ModelAttribute(ADDON) AddonService addonService,
                            @RequestParam("serviceId") Long serviceId,
                            HttpSession session,
                            RedirectAttributes redirectAttributes
    ) {
        Long userId = (Long) session.getAttribute(USER_ID);

        if (userId == null) {
            return REDIRECT_LOGIN; // Redirect to login if the user is not logged in
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));


        double DURATION =  Double.parseDouble(addonService.getDuration());
        if(DURATION < 0 || addonService.getName().isEmpty()){
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE,"Please Choose correct Service duration");
            return REDIRECT_ADDON_NEW;
        }
        // Set the user to the bookingSpot
        addonService.setUser(user);
        Optional<ServiceEntity> serviceEntity = serviceRepository.findById(serviceId);
        if(serviceEntity.isPresent()) {
            ServiceEntity service = serviceEntity.get();
            addonService.setServiceId(service);
            addonServiceHandler.saveAddon(addonService);
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE,"You Selected Service is now Added");
        }
        else{
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE,"Service ID not Found Please Try Again!");
        }

        return REDIRECT_ADDON_NEW;
    }

    @GetMapping("/edit/{id}")
    public String editAddonForm(@PathVariable Long id, Model model, HttpSession session) {
        Long loggedInUser= (Long)session.getAttribute(USER_ID);
        if(loggedInUser==null)
        {
            return REDIRECT_LOGIN;
        }
        List<AddonService> userServices = addonServiceHandler.getAddonByUserId(loggedInUser);
        model.addAttribute(ADDON, userServices);
        return "create-addon";
    }


    @PostMapping("/update/{id}")
    public String updateAddon(@PathVariable Long id, @ModelAttribute AddonService addonService,RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE,"Modification is Successful!");
        addonServiceHandler.updateAddon(id, addonService);
        return "redirect:/addon/all";
    }

    @PostMapping("/delete/{id}")
    public String deleteAddon(@PathVariable Long id,RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE,"Service Successfully Removed!");
        addonServiceHandler.deleteAddonById(id);
        return "redirect:/addon/delete";
    }

    @GetMapping("/modify-duration")
    public String modifyAddonDurationForm(Model model,HttpSession session) {
        Long loggedInUser= (Long)session.getAttribute(USER_ID);
        if(loggedInUser==null)
        {
            return "login";
        }
        List<AddonService> addons = addonServiceHandler.getAllAddons();
        model.addAttribute(ADDONS, addons);
        return "modify-addon-duration";
    }
    @GetMapping("/remove-service")
    public String showRemoveServiceForm(HttpSession session, Model model){
        Long loggedInUser = (Long) session.getAttribute(USER_ID);
        if(loggedInUser == null){
            return REDIRECT_LOGIN;
        }
        List<ServiceEntity> ownerServices = modifyOwnerService.getOwnerServices(loggedInUser);
        if(ownerServices.isEmpty()){
            model.addAttribute(ERROR_MESSAGE, "You currently do not have any services");
        }
        model.addAttribute(OWNER_SERVICES,ownerServices);
        return "RemoveOwnerService";
    }
    @PostMapping("/removeService")
    public String removeService(@RequestParam("id") Long ID,Model model,HttpSession session,RedirectAttributes redirectAttributes){
        Long loggedInUser = (Long) session.getAttribute(USER_ID);
        if(loggedInUser == null){
            return REDIRECT_LOGIN;
        }
        if (ID == null) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Invalid Service ID.");
            return "redirect:/addon/remove-service";
        }
        boolean isCancelled = modifyOwnerService.removeService(ID,loggedInUser);
        if(isCancelled){
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE,"The Selected Service is successfully removed!");
        }else{
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE,"Your Selected Service couldn't be removed!");
        }
        return "redirect:/addon/remove-service";
    }
    @PostMapping("/modify")
    public String modifyAddonDuration(@RequestParam Long addonId, @RequestParam String newDuration,@RequestParam("serviceId") String serviceID, RedirectAttributes redirectAttributes) {
        Long serviceId = Long.parseLong(serviceID);
        Optional<ServiceEntity> serviceEntity = serviceRepository.findById(serviceId);
        if(serviceEntity.isPresent()) {
            Double servicePrice = serviceEntity.get().getPrice();
            Double newPrice = Double.parseDouble(newDuration) * servicePrice;
            addonServiceHandler.updateAddonDuration(addonId, newDuration,newPrice);
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Modification is Successful!");
        }
        else{
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE,"Couldn't Update the Price try Again!");
        }
        return "redirect:/addon/modify-duration";
    }
    @GetMapping("/view-ownerServices")
    public String viewOwnerServices(HttpSession session, RedirectAttributes redirectAttributes,Model model){
        Long loggedInUser = (Long) session.getAttribute(USER_ID);
        if(loggedInUser == null){
            return REDIRECT_LOGIN;
        }
        List<ServiceEntity> services = modifyOwnerService.getOwnerServices(loggedInUser);
        if(services.isEmpty()){
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE,"You Currently Have no services to SHOW!");
        }
        model.addAttribute(OWNER_SERVICES,services);
        return "viewOwnerService";
    }

    @GetMapping("/view")
    public String viewAddonServices(Model model, RedirectAttributes redirectAttributes,HttpSession session) {
        Long loggedInUser = (Long) session.getAttribute(USER_ID);

        if (loggedInUser == null) {
            return REDIRECT_LOGIN; // Redirect to login if user is not logged in
        }
        List<AddonService> addons = addonServiceHandler.getAllAddons();
        if (addons.isEmpty()){
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE,"No Services are Available");
        }
        model.addAttribute(ADDONS, addons);

        return "view-addon-services";
    }

    @GetMapping("/delete")
    public String deleteAddonPage(Model model,HttpSession session) {
        Long loggedInUser = (Long) session.getAttribute(USER_ID);

        if (loggedInUser == null) {
            return REDIRECT_LOGIN; // Redirect to login if user is not logged in
        }
        List<AddonService> addons = addonServiceHandler.getAllAddons();
        model.addAttribute(ADDONS, addons);
        return "delete-service";
    }
}