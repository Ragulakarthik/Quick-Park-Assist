package com.quick_park_assist.serviceImpl;

import com.quick_park_assist.entity.AddonService;

import com.quick_park_assist.repository.AddonRepository;
import com.quick_park_assist.repository.ServiceRepository;
import com.quick_park_assist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AddonServiceHandler {

    @Autowired
    private AddonRepository addonRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ServiceRepository serviceRepository;

    public List<AddonService> getAllAddons() {
        return addonRepository.findAll();
    }

    public void saveAddon(AddonService addonService) {
        addonRepository.save(addonService);
    }

    public Optional<AddonService> getAddonById(Long id) {
        return addonRepository.findById(id);
    }

    public void deleteAddonById(Long id) {
        addonRepository.deleteById(id);
    }

    public void updateAddon(Long id, AddonService updatedAddon) {
        Optional<AddonService> existingAddon = addonRepository.findById(id);
        if (existingAddon.isPresent()) {
            AddonService addon = existingAddon.get();
            addon.setName(updatedAddon.getName());
            addon.setPrice(updatedAddon.getPrice());
            addon.setDuration(updatedAddon.getDuration());
            addonRepository.save(addon);
        }
    }
    // Fetch all addons


    // Update the duration for a specific service
    public void updateAddonDuration(Long id, String newDuration,Double newPrice){
        Optional<AddonService> addon = addonRepository.findById(id);
        if (addon.isPresent()) {
            AddonService existingAddon = addon.get();
            existingAddon.setDuration(newDuration);
            existingAddon.setPrice(newPrice);
            addonRepository.save(existingAddon);
        }
    }

    public List<AddonService> getAddonByUserId(Long loggedInUser) {
        return addonRepository.findByUserId(loggedInUser);
    }
}