package com.haradakatsuya190511;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class HacBookApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(HacBookApiApplication.class, args);
	}

}
