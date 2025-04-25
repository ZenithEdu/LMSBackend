package com.MiniLms.LMSBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Async;

@SpringBootApplication
@Async
public class LmsBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(LmsBackendApplication.class, args);
	}
}
