package com.codependent.isolation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class IsolationLevelsShowcaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(IsolationLevelsShowcaseApplication.class, args);
	}
	
	@Bean
	public String myBean(){
		return new String("1");
	}
}
