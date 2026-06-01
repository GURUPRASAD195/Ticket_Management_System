package com.example.Ticket_Management_System.Util;

import com.example.Ticket_Management_System.entity.Role;
import com.example.Ticket_Management_System.entity.UserEntity;

import java.util.Map;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import java.security.Key;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;



import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtil {
	
	@Value("${jwt.secret}")
	private String secret;
	
	@Value("${jwt.access-token-expiration}")
	private long accessTokenExpiry;
	
	private Key key;
	
	@PostConstruct
	public void init() {
		this.key = Keys.hmacShaKeyFor(secret.getBytes());
	}
	
	public String generateAccessToken(UserEntity user, boolean isGoogle) {
		Map<String,Object> claims = new  HashMap<>();
		claims.put("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
		claims.put("passwordSet", user.isPasswordSet());
		claims.put("isGoogle", isGoogle);
		
		return Jwts.builder().setClaims(claims).setSubject(user.getEmail()).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiry))
				.signWith(key,SignatureAlgorithm.HS256).compact();
	}
	public boolean isTokenValid(String token,UserDetails userDetails) {
		final String email = extractEmail(token);
		return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
	}
	
	public boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
		
	}
	
	public Date extractExpiration(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getExpiration();
	}
	
	public String extractEmail(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
	}

	public boolean isGoogleUser(String token) {
		if (token == null) return false;
		try {
			return (boolean) Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get("isGoogle");
		} catch (Exception e) {
			return false;
		}
	}
}
