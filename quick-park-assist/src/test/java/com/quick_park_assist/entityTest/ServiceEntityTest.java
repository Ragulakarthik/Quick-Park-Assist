package com.quick_park_assist.entityTest;
import static org.junit.jupiter.api.Assertions.*;

import com.quick_park_assist.entity.ServiceEntity;
import com.quick_park_assist.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServiceEntityTest {

    private ServiceEntity serviceEntity;
    private User user;

    @BeforeEach
    public void setUp() {
        serviceEntity = new ServiceEntity();
        user = new User();
        user.setId(1L);
        user.setFullName("Test User");
    }

    @Test
    public void testSetAndGetId() {
        serviceEntity.setId(1L);
        assertEquals(1L, serviceEntity.getId());
    }

    @Test
    public void testSetAndGetName() {
        String name = "Car Wash";
        serviceEntity.setName(name);
        assertEquals(name, serviceEntity.getName());
    }

    @Test
    public void testSetAndGetPrice() {
        Double price = 19.99;
        serviceEntity.setPrice(price);
        assertEquals(price, serviceEntity.getPrice());
    }

    @Test
    public void testSetAndGetDescription() {
        String description = "Basic car wash service.";
        serviceEntity.setDescription(description);
        assertEquals(description, serviceEntity.getDescription());
    }

    @Test
    public void testSetAndGetUser() {
        serviceEntity.setUser(user);
        assertEquals(user, serviceEntity.getUser());
    }

    @Test
    public void testDefaultValues() {
        assertNull(serviceEntity.getId());
        assertNull(serviceEntity.getName());
        assertNull(serviceEntity.getPrice());
        assertNull(serviceEntity.getDescription());
        assertNull(serviceEntity.getUser());
    }
}
