package com.haradakatsuya190511.filters;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.services.JwtService;
import com.haradakatsuya190511.services.TokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;

class JwtAuthenticationFilterTest {
	
	private TokenService tokenService;
	private JwtService jwtService;
	private JwtAuthenticationFilter filter;
	
	@BeforeEach
	void setUp() {
		tokenService = mock(TokenService.class);
		jwtService = mock(JwtService.class);
		filter = new JwtAuthenticationFilter(tokenService, jwtService);
		SecurityContextHolder.clearContext();
	}
	
	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
	}
	
	@Test
	void whenAuthenticationAlreadyExists_thenJustPassThrough() throws ServletException, IOException {
		SecurityContextHolder.getContext().setAuthentication(
			new UsernamePasswordAuthenticationToken("already", null)
		);
		
		MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/test");
		MockHttpServletResponse res = new MockHttpServletResponse();
		FilterChain chain = mock(FilterChain.class);
		
		filter.doFilterInternal(req, res, chain);
		
		verify(chain, times(1)).doFilter(req, res);
		verifyNoInteractions(tokenService);
		verifyNoInteractions(jwtService);
	}
	
	@Test
    void whenNoCookie_thenNoAuthenticationAndPassThrough() throws ServletException, IOException {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/test");
        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        when(tokenService.getTokenCookie(req)).thenReturn(null);

        filter.doFilterInternal(req, res, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(tokenService, times(1)).getTokenCookie(req);
        verifyNoInteractions(jwtService);
        verify(chain, times(1)).doFilter(req, res);
    }
	
	@Test
	void whenCookieExistsButJwtInvalid_thenNoAuthenticationAndPassThrough() throws ServletException, IOException {
		MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/test");
		MockHttpServletResponse res = new MockHttpServletResponse();
		FilterChain chain = mock(FilterChain.class);
		
		Cookie cookie = new Cookie("access_token", "bad.token");
		when(tokenService.getTokenCookie(req)).thenReturn(cookie);
		when(jwtService.getUserIfValid("bad.token")).thenReturn(Optional.empty());
		
		filter.doFilterInternal(req, res, chain);
		
		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
		verify(tokenService, times(1)).getTokenCookie(req);
		verify(jwtService, times(1)).getUserIfValid("bad.token");
		verify(chain, times(1)).doFilter(req, res);
	}
	
	@Test
	void whenCookieExistsAndJwtValid_thenSetsAuthenticationAndPassThrough() throws ServletException, IOException {
		MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/test");
		MockHttpServletResponse res = new MockHttpServletResponse();
		FilterChain chain = mock(FilterChain.class);
		
		Cookie cookie = new Cookie("access_token", "good.token");
		when(tokenService.getTokenCookie(req)).thenReturn(cookie);
		
		User user = new User();
		when(jwtService.getUserIfValid("good.token")).thenReturn(Optional.of(user));
		
		filter.doFilterInternal(req, res, chain);
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		assertThat(auth).isNotNull();
		assertThat(auth.getPrincipal()).isSameAs(user);
		assertThat(auth.getDetails()).isNotNull();
		assertThat(auth.getDetails()).isInstanceOf(WebAuthenticationDetails.class);
		
		verify(tokenService, times(1)).getTokenCookie(req);
		verify(jwtService, times(1)).getUserIfValid("good.token");
		verify(chain, times(1)).doFilter(req, res);
	}
}
