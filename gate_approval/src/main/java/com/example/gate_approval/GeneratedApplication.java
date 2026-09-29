package com.example.gate_approval;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GeneratedApplication {

	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.configure().load();
		// Set system properties from .env
		dotenv.entries().forEach(entry -> {
			System.setProperty(entry.getKey(), entry.getValue());
		});
		SpringApplication.run(GeneratedApplication.class, args);
	}

}
