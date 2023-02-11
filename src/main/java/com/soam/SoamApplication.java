package com.soam;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

@SpringBootApplication
public class SoamApplication {

	@Autowired
	private MessageSource messageSource;

	public static void main(String[] args) {
		System.setProperty("spring.config.additional-location", "optional:file:./soam.properties");
		SpringApplication.run(SoamApplication.class, args);
	}

	@PostConstruct
	public void test() {
		System.out.println(messageSource.getMessage("header.home", null, Locale.FRENCH));
	}
}
