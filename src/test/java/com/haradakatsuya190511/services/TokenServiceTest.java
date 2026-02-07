package com.haradakatsuya190511.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {
	
	@Mock
	HttpServletRequest request;
	
	@InjectMocks
	TokenService tokenService;
	
	@Test
	void getTokenCookie_exists() {
		Cookie tokenCookie = new Cookie("token", "jwt-token");
		Cookie otherCookie = new Cookie("other", "value");
		when(request.getCookies()).thenReturn(new Cookie[] {otherCookie, tokenCookie});
		Cookie result = tokenService.getTokenCookie(request);
		assertNotNull(result);
		assertEquals("token", result.getName());
		assertEquals("jwt-token", result.getValue());
	}
	
	@Test
	void getTokenCookie_notExists() {
		Cookie otherCookie = new Cookie("other", "value");
		when(request.getCookies()).thenReturn(new Cookie[] {otherCookie});
		Cookie result = tokenService.getTokenCookie(request);
		assertNull(result);
	}
	
	@Test
	void getTokenCookie_cookiesNull() {
		when(request.getCookies()).thenReturn(null);
		Cookie result = tokenService.getTokenCookie(request);
		assertNull(result);
	}
}
