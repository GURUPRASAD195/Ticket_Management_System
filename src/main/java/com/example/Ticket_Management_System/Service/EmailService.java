package com.example.Ticket_Management_System.Service;

public interface EmailService {

	void sendEmail(String[] to, String subject, String text);

}
