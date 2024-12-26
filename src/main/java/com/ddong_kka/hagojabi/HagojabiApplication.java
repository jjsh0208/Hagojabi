package com.ddong_kka.hagojabi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HagojabiApplication {
	public static void main(String[] args) {
		SpringApplication.run(HagojabiApplication.class, args);
	}
}