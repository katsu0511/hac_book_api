package com.haradakatsuya190511.utils;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.haradakatsuya190511.config.JwtSecretProperties;
import com.haradakatsuya190511.entities.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	
	private final SecretKey secretKey;
	private final long expirationMs = 3600000;
	
	public JwtUtil(JwtSecretProperties jwtSecretProperties) {
		this.secretKey = Keys.hmacShaKeyFor(jwtSecretProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
	}
	
	public String generateToken(User user) {
		return Jwts.builder()
			.subject(user.getEmail())
			.claim("name", user.getName())
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis() + expirationMs))
			.signWith(secretKey)
			.compact();
	}
	
	public SecretKey getSecretKey() {
		return secretKey;
	}
}
