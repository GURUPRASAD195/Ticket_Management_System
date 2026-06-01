package com.example.Ticket_Management_System.Controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.Ticket_Management_System.Service.TicketService;
import com.example.Ticket_Management_System.entity.Ticket;
import com.example.Ticket_Management_System.entity.UserEntity;
import com.example.Ticket_Management_System.Repository.UserRepository;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private UserRepository userRepository;


    // USER → Create Ticket
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping
    public Ticket createTicket(@RequestBody Ticket ticket,
                               Principal principal) {

        UserEntity user = userRepository
                .findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ticketService.createTicket(
                ticket.getTitle(),
                ticket.getDescription(),
                user
        );
    }

    // USER → View My Tickets
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/my")
    public List<Ticket> getMyTickets(Principal principal) {

        UserEntity user = userRepository
                .findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ticketService.getTicketsByUser(user);
    }

    

    // ADMIN → View All Tickets
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/all")
    public List<Ticket> getAllTickets() {
        return ticketService.getAllTickets();
    }

    // ADMIN → Assign Ticket to Agent
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/assign")
    public Ticket assignTicket(@RequestParam Long ticketId,
                               @RequestParam String agentEmail) {

        UserEntity agent = userRepository
                .findByEmail(agentEmail)
                .orElseThrow(() -> new RuntimeException("Agent not found"));

        return ticketService.assignTicket(ticketId, agent);
    }

    

    // AGENT → View Assigned Tickets
    @PreAuthorize("hasAuthority('ROLE_AGENT')")
    @GetMapping("/assigned")
    public List<Ticket> getAssignedTickets(Principal principal) {

        UserEntity agent = userRepository
                .findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Agent not found"));

        return ticketService.getTicketsByAgent(agent);
    }

    // AGENT → Update Ticket Status
    @PreAuthorize("hasAuthority('ROLE_AGENT')")
    @PutMapping("/status")
    public Ticket updateStatus(@RequestParam Long ticketId,
                               @RequestParam String status) {

        return ticketService.updateStatus(ticketId, status);
    }
}
