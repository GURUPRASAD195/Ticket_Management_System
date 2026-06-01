package com.example.Ticket_Management_System.Scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.Ticket_Management_System.Service.EmailService;

@Component
public class EmailScheduler {	

	@Autowired
	private EmailService emailService; // Directly inject the class

	@Scheduled(cron = "0 0/15 * * * ?") // every 10 minutes
	public void sendScheduledEmail() {

		// List of recipients
		String[] recipients = { ""};

		emailService.sendEmail(recipients, "Scheduled Email from Spring Boot",
				"Hello! This is a scheduled email sent automatically every 10 minutes.");
	}
}
