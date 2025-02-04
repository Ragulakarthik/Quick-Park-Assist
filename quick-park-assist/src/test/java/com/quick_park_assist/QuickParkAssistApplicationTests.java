package com.quick_park_assist;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@SpringBootTest
class QuickParkAssistApplicationTests {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	void contextLoads() {
		// Verifies that the application context loads without errors
		assertThat(applicationContext).isNotNull();
	}

	@Test
	void mainApplicationClassLoads() {
		// Ensures that the main application class is loaded into the context
		boolean isBeanPresent = applicationContext.containsBean("quickParkAssistApplication");
		assertThat(isBeanPresent).isTrue();
	}

}
