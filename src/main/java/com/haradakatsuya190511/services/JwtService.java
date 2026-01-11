package com.haradakatsuya190511.services;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.haradakatsuya190511.utils.JwtUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Service
public class JwtService {
	
	private final JwtUtil jwtUtil;
	private final CustomUserDetailsService userDetailsService;
	
	public JwtService(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
		this.jwtUtil = jwtUtil;
		this.userDetailsService = userDetailsService;
	}
	
	public Optional<UserDetails> getUserDetailsIfValid(String token) {
		try {
			Claims claims = Jwts.parser()
				.verifyWith(jwtUtil.getSecretKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
			Date expiration = claims.getExpiration();
			if (expiration != null && expiration.toInstant().isAfter(Instant.now())) {
				return Optional.of(userDetailsService.loadUserByUsername(claims.getSubject()));
			}
			return Optional.empty();
		} catch (Exception e) {
			return Optional.empty();
		}
	}
}
