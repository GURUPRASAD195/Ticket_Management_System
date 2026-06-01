package com.example.Ticket_Management_System.Controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestRoleController {

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public String userAccess() {
        return "User logged in";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String adminAccess() {
        return "Admin logged in";
    }

    @PreAuthorize("hasRole('AGENT')")
    @GetMapping("/agent")
    public String agentAccess() {
        return "Agent logged in";
    }
}
