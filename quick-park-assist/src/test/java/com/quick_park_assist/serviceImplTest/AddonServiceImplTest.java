package com.quick_park_assist.serviceImplTest;

import com.quick_park_assist.entity.AddonService;
import com.quick_park_assist.entity.ServiceEntity;
import com.quick_park_assist.entity.User;
import com.quick_park_assist.repository.AddonRepository;
import com.quick_park_assist.repository.ServiceRepository;
import com.quick_park_assist.repository.UserRepository;
import com.quick_park_assist.serviceImpl.AddonServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AddonServiceImplTest {

    @Mock
    private AddonRepository addonRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @InjectMocks
    private AddonServiceImpl addonServiceImpl;

    private AddonService testAddon;
    private User testUser;
    private ServiceEntity testService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        testUser = new User();
        testUser.setId(1L);

        testService = new ServiceEntity();
        testService.setId(1L);
        testService.setName("Test Service");
        testService.setPrice(100.0);

        testAddon = new AddonService();
        testAddon.setId(1L);
        testAddon.setName("Test Addon");
        testAddon.setPrice(50.0);
        testAddon.setDuration("2");
        testAddon.setUser(testUser);
        testAddon.setServiceId(testService);
    }

    @Test
    public void testGetAllAddons_ShouldReturnList() {
        List<AddonService> expectedList = new ArrayList<>();
        expectedList.add(testAddon);
        when(addonRepository.findAll()).thenReturn(expectedList);

        List<AddonService> result = addonServiceImpl.getAllAddons();

        assertEquals(expectedList, result);
        verify(addonRepository).findAll();
    }

    @Test
    public void testSaveAddon_ShouldSaveSuccessfully() {
        addonServiceImpl.saveAddon(testAddon);
        verify(addonRepository).save(testAddon);
    }

    @Test
    public void testGetAddonById_WhenExists_ShouldReturn() {
        when(addonRepository.findById(1L)).thenReturn(Optional.of(testAddon));

        Optional<AddonService> result = addonServiceImpl.getAddonById(1L);

        assertTrue(result.isPresent());
        assertEquals(testAddon, result.get());
    }

    @Test
    public void testGetAddonById_WhenNotExists_ShouldReturnEmpty() {
        when(addonRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<AddonService> result = addonServiceImpl.getAddonById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    public void testDeleteAddonById_ShouldDelete() {
        addonServiceImpl.deleteAddonById(1L);
        verify(addonRepository).deleteById(1L);
    }

    @Test
    public void testUpdateAddon_WhenExists_ShouldUpdate() {
        when(addonRepository.findById(1L)).thenReturn(Optional.of(testAddon));
        
        AddonService updatedAddon = new AddonService();
        updatedAddon.setName("Updated Name");
        updatedAddon.setPrice(75.0);
        updatedAddon.setDuration("3");

        addonServiceImpl.updateAddon(1L, updatedAddon);

        verify(addonRepository).save(any(AddonService.class));
    }

    @Test
    public void testUpdateAddonDuration_WhenExists_ShouldUpdate() {
        when(addonRepository.findById(1L)).thenReturn(Optional.of(testAddon));

        addonServiceImpl.updateAddonDuration(1L, "3", 75.0);

        verify(addonRepository).save(any(AddonService.class));
    }

    @Test
    public void testGetAddonByUserId_ShouldReturnUserAddons() {
        List<AddonService> expectedList = new ArrayList<>();
        expectedList.add(testAddon);
        when(addonRepository.findByUserId(1L)).thenReturn(expectedList);

        List<AddonService> result = addonServiceImpl.getAddonByUserId(1L);

        assertEquals(expectedList, result);
        verify(addonRepository).findByUserId(1L);
    }
}