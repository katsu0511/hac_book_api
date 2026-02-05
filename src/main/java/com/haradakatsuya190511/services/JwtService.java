package com.haradakatsuya190511.services;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.repositories.UserRepository;
import com.haradakatsuya190511.utils.JwtUtil;

import io.jsonwebtoken.Claims;

@Service
public class JwtService {
	
	private final JwtUtil jwtUtil;
	private final UserRepository userRepository;
	
	public JwtService(JwtUtil jwtUtil, UserRepository userRepository) {
		this.jwtUtil = jwtUtil;
		this.userRepository = userRepository;
	}
	
	public Optional<User> getUserIfValid(String token) {
		try {
			Claims claims = jwtUtil.parseToken(token);
			Date expiration = claims.getExpiration();
			if (expiration != null && expiration.toInstant().isAfter(Instant.now())) {
				Long userId = Long.valueOf(claims.getSubject());
				User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found."));
				return Optional.of(user);
			}
			return Optional.empty();
		} catch (Exception e) {
			return Optional.empty();
		}
	}
}
