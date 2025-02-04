package com.quick_park_assist.service;

import com.quick_park_assist.entity.AddonService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public interface IAddonService {
    List<AddonService> getAllAddons();
    void saveAddon(AddonService addonService);
    Optional<AddonService> getAddonById(Long id);
    void deleteAddonById(Long id);
    void updateAddon(Long id, AddonService updatedAddon);
    void updateAddonDuration(Long id, String newDuration,Double newPrice);
    List<AddonService> getAddonByUserId(Long loggedInUser);
}
