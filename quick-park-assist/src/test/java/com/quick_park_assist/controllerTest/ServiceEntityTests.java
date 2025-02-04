package com.quick_park_assist.controllerTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.quick_park_assist.entity.ServiceEntity;
import com.quick_park_assist.entity.User;
import static org.junit.jupiter.api.Assertions.*;


public class ServiceEntityTests {

    private ServiceEntity serviceEntity;

    @BeforeEach
    public void setUp() {
        serviceEntity = new ServiceEntity();
    }

    @Test
    public void testId() {
        serviceEntity.setId(1L);
        assertEquals(1L, serviceEntity.getId());
    }

    @Test
    public void testName() {
        String name = "Test Service";
        serviceEntity.setName(name);
        assertEquals(name, serviceEntity.getName());
    }

    @Test
    public void testPrice() {
        Double price = 150.0;
        serviceEntity.setPrice(price);
        assertEquals(price, serviceEntity.getPrice());
    }

    @Test
    public void testDescription() {
        String description = "This is a test service description.";
        serviceEntity.setDescription(description);
        assertEquals(description, serviceEntity.getDescription());
    }

    @Test
    public void testUser() {
        User user = new User(); // Ensure that User class is properly defined in your project.
        serviceEntity.setUser(user);
        assertEquals(user, serviceEntity.getUser());
    }
}
