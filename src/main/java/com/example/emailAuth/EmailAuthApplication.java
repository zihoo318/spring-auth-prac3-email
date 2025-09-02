package com.example.emailAuth;

import com.example.emailAuth.emailSender.EmailProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(EmailProperties.class)
public class EmailAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmailAuthApplication.class, args);
	}

}
