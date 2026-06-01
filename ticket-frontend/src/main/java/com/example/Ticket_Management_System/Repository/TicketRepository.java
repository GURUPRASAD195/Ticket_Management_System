package com.example.Ticket_Management_System.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Ticket_Management_System.entity.Ticket;
import com.example.Ticket_Management_System.entity.UserEntity;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // Get all tickets created by a user
    List<Ticket> findByCreatedBy(UserEntity user);

    // Get all tickets assigned to an agent
    List<Ticket> findByAssignedTo(UserEntity agent);

}
