package com.example.Ticket_Management_System.Config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.Ticket_Management_System.Repository.RoleRepository;
import com.example.Ticket_Management_System.entity.Role;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {

            if (roleRepository.findByName("ROLE_USER").isEmpty()) {

                Role user = new Role();
                user.setName("ROLE_USER");
                user.setDescription("Normal user");

                Role agent = new Role();
                agent.setName("ROLE_AGENT");
                agent.setDescription("Support agent");

                Role admin = new Role();
                admin.setName("ROLE_ADMIN");
                admin.setDescription("System admin");

                roleRepository.saveAll(List.of(user, agent, admin));
            }
        };
    }
}
