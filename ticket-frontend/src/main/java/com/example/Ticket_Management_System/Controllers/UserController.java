package com.example.Ticket_Management_System.Controllers;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/users")
public class UserController {
	
	
	@GetMapping
	public String GetAllUsers(){
		return "hello";
	}
	
	@PostMapping
	public void CreateUser() {
		
	}
	
	@DeleteMapping("/{id}")
	public void DeleteUser() {
		
	}
	
	
}
