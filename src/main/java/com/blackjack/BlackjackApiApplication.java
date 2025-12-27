package com.blackjack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class BlackjackApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlackjackApiApplication.class, args);
	}

}
