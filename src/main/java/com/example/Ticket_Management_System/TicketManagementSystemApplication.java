package com.example.Ticket_Management_System;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableScheduling
public class TicketManagementSystemApplication {

	public static void main(String[] args) {
	    System.out.println(new BCryptPasswordEncoder().encode("777777"));
	    SpringApplication.run(TicketManagementSystemApplication.class, args);
	}


}
