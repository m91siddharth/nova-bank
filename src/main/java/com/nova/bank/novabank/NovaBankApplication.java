package com.nova.bank.novabank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NovaBankApplication {

	public static void main(String[] args) {
		SpringApplication.run(NovaBankApplication.class, args);
	}

}
