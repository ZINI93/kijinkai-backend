package com.kijinkai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class KijinkaiApplication {

	public static void main(String[] args) {
		SpringApplication.run(KijinkaiApplication.class, args);
	}

}
