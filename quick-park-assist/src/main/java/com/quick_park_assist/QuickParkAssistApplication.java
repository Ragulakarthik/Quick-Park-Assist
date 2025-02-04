package com.quick_park_assist;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = "com.quick_park_assist")

public class QuickParkAssistApplication {
	public static final Logger log = LogManager.getLogger(QuickParkAssistApplication.class);
	public static void main(String[] args) {
		log.info("My Application is starting");
		SpringApplication.run(QuickParkAssistApplication.class, args);
	}
}