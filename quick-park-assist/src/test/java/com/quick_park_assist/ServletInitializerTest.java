package com.quick_park_assist;


import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ServletInitializerTest {

    @Test
    void testConfigure() {
        // Arrange
        ServletInitializer servletInitializer = new ServletInitializer();
        SpringApplicationBuilder builder = new SpringApplicationBuilder();

        // Act
        SpringApplicationBuilder result = servletInitializer.configure(builder);

        // Assert
        assertNotNull(result, "The returned SpringApplicationBuilder should not be null");
    }
}
