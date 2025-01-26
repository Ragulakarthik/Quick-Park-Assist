package com.quick_park_assist;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure( SpringApplicationBuilder application) {
		return application.sources(QuickParkAssistApplication.class);
	}

}
