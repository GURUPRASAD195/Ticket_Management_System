package com.example.Ticket_Management_System.Service;

import java.util.List;

import com.example.Ticket_Management_System.entity.Ticket;
import com.example.Ticket_Management_System.entity.UserEntity;

public interface TicketService {

    // User creates ticket
    Ticket createTicket(String title, String description, UserEntity user);

    // Admin assigns ticket to agent
    Ticket assignTicket(Long ticketId, UserEntity agent);

    // Agent updates ticket status
    Ticket updateStatus(Long ticketId, String status);

    // Get tickets created by user
    List<Ticket> getTicketsByUser(UserEntity user);

    // Get tickets assigned to agent
    List<Ticket> getTicketsByAgent(UserEntity agent);
    
    List<Ticket> getAllTickets();
}
