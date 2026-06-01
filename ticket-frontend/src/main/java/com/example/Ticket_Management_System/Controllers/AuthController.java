package com.example.Ticket_Management_System.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.Ticket_Management_System.dto.LoginRequestDto;
import com.example.Ticket_Management_System.dto.GoogleLoginRequest;
import com.example.Ticket_Management_System.dto.ChangePasswordRequest;
import com.example.Ticket_Management_System.entity.UserEntity;
import com.example.Ticket_Management_System.entity.Role;
import com.example.Ticket_Management_System.Repository.UserRepository;
import com.example.Ticket_Management_System.Repository.RoleRepository;
import com.example.Ticket_Management_System.Util.JwtUtil;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import java.util.*;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Value("${google.client.id}")
    private String googleClientId;


    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
            );

            UserEntity user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));
            String token = jwtUtil.generateAccessToken(user);

            return ResponseEntity.ok(token);

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }

    @PostMapping("/google-login")
    public ResponseEntity<String> googleLogin(@RequestBody GoogleLoginRequest request) {
        try {
            System.out.println("Processing Google Login for token: " + (request.getIdToken() != null ? "Token Received" : "null"));
            System.out.println("Using Client ID: " + googleClientId);

            if ("MOCK_DEVELOPMENT_TOKEN".equals(request.getIdToken())) {
                System.out.println("Bypassing verification for MOCK token");
                String email = "demo-google-user@example.com";
                UserEntity user = userRepository.findByEmail(email).orElseGet(() -> {
                    UserEntity newUser = new UserEntity();
                    newUser.setEmail(email);
                    newUser.setPassword(passwordEncoder.encode("demo-pass"));
                    newUser.setEnabled(true);
                    newUser.setPasswordSet(true); // Mock user has password set
                    Role role = roleRepository.findByName("ROLE_USER").orElse(null);
                    if (role != null) newUser.setRoles(Collections.singleton(role));
                    return userRepository.save(newUser);
                });
                return ResponseEntity.ok(jwtUtil.generateAccessToken(user, true)); // Mock Google user
            }

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(request.getIdToken());
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                System.out.println("Verified Google email: " + email);

                Optional<UserEntity> userOptional = userRepository.findByEmail(email);
                UserEntity user;

                if (userOptional.isPresent()) {
                    user = userOptional.get();
                    // If user existed before Google login, they already have a password set
                    if (!user.isPasswordSet()) {
                        user.setPasswordSet(true);
                        userRepository.save(user);
                    }
                } else {
                    System.out.println("Creating new user for: " + email);
                    user = new UserEntity();
                    user.setEmail(email);
                    user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                    user.setEnabled(true);
                    user.setPasswordSet(false); // New Google users don't have a known password

                    Role userRole = roleRepository.findByName("ROLE_USER")
                            .orElseThrow(() -> new RuntimeException("Default role ROLE_USER not found"));
                    Set<Role> roles = new HashSet<>();
                    roles.add(userRole);
                    user.setRoles(roles);

                    userRepository.save(user);
                }

                String token = jwtUtil.generateAccessToken(user, true); // Real Google user
                return ResponseEntity.ok(token);
            } else {
                System.err.println("Google ID Token verification failed (returned null)");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Google ID Token");
            }
        } catch (Exception e) {
            System.err.println("Error verifying Google Token: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error verifying Google Token: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        String roleName = request.get("role"); 

        if (roleName == null || roleName.isEmpty()) {
            roleName = "ROLE_USER"; 
        }

        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        UserEntity newUser = new UserEntity();
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setPasswordSet(true);
        
        Role roleEntity = roleRepository.findByName(roleName).orElse(null);
        if (roleEntity == null) {
            String formattedRole = "ROLE_" + roleName.toUpperCase();
            roleEntity = roleRepository.findByName(formattedRole).orElse(null);
        }

        if (roleEntity == null) {
             return ResponseEntity.badRequest().body("Role not found in DB: " + roleName);
        }

        Set<Role> roles = new HashSet<>();
        roles.add(roleEntity);
        newUser.setRoles(new HashSet<>(Collections.singletonList(roleEntity)));
        userRepository.save(newUser);

        return ResponseEntity.ok(jwtUtil.generateAccessToken(newUser, false)); // Regular registration, not Google
    }

    @GetMapping("/users")
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            
            if (email == null || email.equals("anonymousUser")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }

            UserEntity user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

            // Logic: Authenticated users via Google can bypass the current password check.
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String rawToken = (String) auth.getCredentials();
            boolean isGoogleUser = jwtUtil.isGoogleUser(rawToken);
            
            if (user.isPasswordSet()) {
                if (!isGoogleUser) {
                    if (request.getCurrentPassword() == null || request.getCurrentPassword().trim().isEmpty()) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Current password is required to change password");
                    }
                    if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Current password is incorrect");
                    }
                } else {
                    // Google user bypass
                    System.out.println("Google User detected. Bypassing current password check for: " + email);
                    if (request.getCurrentPassword() != null && !request.getCurrentPassword().trim().isEmpty()) {
                        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                             System.out.println("Warning: Google user provided an incorrect password, but we allowed the change anyway.");
                        }
                    }
                }
            }
 else {
                // If user.isPasswordSet() is false, it means they logged in via Google and haven't set a password yet.
                // In this case, we don't require a current password.
                System.out.println("User has no password set, bypassing current password check for: " + email);
            }

            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            user.setPasswordSet(true); // Password is now explicitly set
            userRepository.save(user);

            return ResponseEntity.ok("Password changed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating password: " + e.getMessage());
        }
    }
}
