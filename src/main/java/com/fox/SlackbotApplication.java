package com.fox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"me.ramswaroop.jbot", "com.fox"})
public class SlackbotApplication {

	public static void main(String[] args) {
		SpringApplication.run(SlackbotApplication.class, args);
	}
}
