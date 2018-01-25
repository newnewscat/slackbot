package com.fox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication//(scanBasePackages = {"me.ramswaroop.jbot", "com.fox"})
@EnableScheduling
@ComponentScan(basePackages ={"me.ramswaroop.jbot", "com.fox"})
public class SlackbotApplication {

	public static void main(String[] args) {
		SpringApplication.run(SlackbotApplication.class, args);
	}
}
