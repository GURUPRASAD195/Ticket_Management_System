package com.example.Ticket_Management_System.ServiceImplementation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Ticket_Management_System.Repository.TicketRepository;
import com.example.Ticket_Management_System.Service.TicketService;
import com.example.Ticket_Management_System.entity.Ticket;
import com.example.Ticket_Management_System.entity.UserEntity;
import com.example.Ticket_Management_System.Service.NotificationService;
import com.example.Ticket_Management_System.Service.EmailService;

@Service
public class TicketServiceImpl implements TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmailService emailService;

    @Override
    public Ticket createTicket(String title, String description, UserEntity user) {
        Ticket ticket = new Ticket();
        ticket.setTitle(title);
        ticket.setDescription(description);
        ticket.setCreatedBy(user);
        return ticketRepository.save(ticket);
    }

    @Override
    public Ticket assignTicket(Long ticketId, UserEntity agent) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow();
        ticket.setAssignedTo(agent);
        Ticket savedTicket = ticketRepository.save(ticket);
        
        // Notify User
        String message = "Your ticket '" + savedTicket.getTitle() + "' has been assigned to agent: " + agent.getEmail();
        notificationService.createNotification(message, savedTicket.getCreatedBy());
        
        // Email User
        emailService.sendEmail(
            new String[]{savedTicket.getCreatedBy().getEmail()},
            "Ticket Assigned: " + savedTicket.getTitle(),
            message
        );

        return savedTicket;
    }

    @Override
    public Ticket updateStatus(Long ticketId, String status) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow();
        ticket.setStatus(status);
        Ticket savedTicket = ticketRepository.save(ticket);

        // Notify User
        String message = "Your ticket '" + savedTicket.getTitle() + "' status updated to: " + status;
        notificationService.createNotification(message, savedTicket.getCreatedBy());

        // Email User
        emailService.sendEmail(
            new String[]{savedTicket.getCreatedBy().getEmail()},
            "Ticket Status Updated: " + savedTicket.getTitle(),
            message
        );

        return savedTicket;
    }

    @Override
    public List<Ticket> getTicketsByUser(UserEntity user) {
        return ticketRepository.findByCreatedBy(user);
    }

    @Override
    public List<Ticket> getTicketsByAgent(UserEntity agent) {
        return ticketRepository.findByAssignedTo(agent);
    }
    
    @Override
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

}
