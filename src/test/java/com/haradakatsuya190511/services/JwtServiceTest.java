package com.haradakatsuya190511.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.repositories.UserRepository;
import com.haradakatsuya190511.utils.JwtUtil;

import io.jsonwebtoken.Claims;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {
	
	@Mock
	JwtUtil jwtUtil;
	
	@Mock
	UserRepository userRepository;
	
	@InjectMocks
	JwtService jwtService;
	
	@Test
	void getUserIfValid_success() {
		User user = new User("John", "test@example.com", "hashed");
		Claims claims = mock(Claims.class);
		when(jwtUtil.parseToken("valid-token")).thenReturn(claims);
		when(claims.getExpiration()).thenReturn(Date.from(Instant.now().plusSeconds(60)));
		when(claims.getSubject()).thenReturn("1");
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		Optional<User> result = jwtService.getUserIfValid("valid-token");
		assertTrue(result.isPresent());
		assertEquals(user, result.get());
	}
	
	@Test
	void getUserIfValid_expirationIsNull_returnsEmpty() {
		Claims claims = mock(Claims.class);
		when(jwtUtil.parseToken("valid-token")).thenReturn(claims);
		when(claims.getExpiration()).thenReturn(null);
		Optional<User> result = jwtService.getUserIfValid("valid-token");
		assertTrue(result.isEmpty());
	}
	
	@Test
	void getUserIfValid_expiredToken_returnsEmpty() {
		Claims claims = mock(Claims.class);
		when(jwtUtil.parseToken("valid-token")).thenReturn(claims);
		when(claims.getExpiration()).thenReturn(Date.from(Instant.now().minusSeconds(60)));
		Optional<User> result = jwtService.getUserIfValid("expired-token");
		assertTrue(result.isEmpty());
	}
	
	@Test
	void getUserIfValid_userNotFound_returnsEmpty() {
		Claims claims = mock(Claims.class);
		when(jwtUtil.parseToken("valid-token")).thenReturn(claims);
		when(claims.getExpiration()).thenReturn(Date.from(Instant.now().plusSeconds(60)));
		when(claims.getSubject()).thenReturn("1");
		when(userRepository.findById(1L)).thenReturn(Optional.empty());
		Optional<User> result = jwtService.getUserIfValid("valid-token");
		assertTrue(result.isEmpty());
	}
	
	@Test
	void getUserIfValid_invalidToken_returnsEmpty() {
		Optional<User> result = jwtService.getUserIfValid("invalid-token");
		assertTrue(result.isEmpty());
	}
}
