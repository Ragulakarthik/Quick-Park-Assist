package com.quick_park_assist.controllerTest;

import com.quick_park_assist.entity.AddonService;
import com.quick_park_assist.entity.ServiceEntity;
import com.quick_park_assist.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



import static org.junit.jupiter.api.Assertions.*;

public class AddonServiceTests {

    private AddonService addonService;

    @BeforeEach
    public void setUp() {
        addonService = new AddonService();
    }

    @Test
    public void testId() {
        addonService.setId(1L);
        assertEquals(1L, addonService.getId());
    }

    @Test
    public void testName() {
        String name = "Test Addon";
        addonService.setName(name);
        assertEquals(name, addonService.getName());
    }

    @Test
    public void testPrice() {
        Double price = 100.0;
        addonService.setPrice(price);
        assertEquals(price, addonService.getPrice());
    }

    @Test
    public void testDuration() {
        String duration = "1 hour";
        addonService.setDuration(duration);
        assertEquals(duration, addonService.getDuration());
    }

    @Test
    public void testServiceId() {
        ServiceEntity serviceEntity = new ServiceEntity();
        addonService.setServiceId(serviceEntity);
        assertEquals(serviceEntity, addonService.getServiceId());
    }

    @Test
    public void testUser() {
        User user = new User();
        addonService.setUser(user);
        assertEquals(user, addonService.getUser());
    }
}
