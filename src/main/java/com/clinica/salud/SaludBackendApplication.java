package com.clinica.salud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SaludBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaludBackendApplication.class, args);
	}

}
