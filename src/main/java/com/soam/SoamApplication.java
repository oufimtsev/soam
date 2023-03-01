package com.soam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SoamApplication {
	private static final String EXTERNAL_CONFIG_FILE = "soam.properties";

	public static void main(String[] args) {
		System.setProperty("spring.config.additional-location", "optional:file:./" + EXTERNAL_CONFIG_FILE);
		SpringApplication.run(SoamApplication.class, args);
	}

}
