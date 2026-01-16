package com.haradakatsuya190511.filters;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.haradakatsuya190511.services.JwtService;
import com.haradakatsuya190511.services.TokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	
	private final TokenService tokenService;
	private final JwtService jwtService;
	
	public JwtAuthenticationFilter(TokenService tokenService, JwtService jwtService) {
		this.tokenService = tokenService;
		this.jwtService = jwtService;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			filterChain.doFilter(request, response);
			return;
		}
		
		Cookie tokenCookie = tokenService.getTokenCookie(request);
		String token = Optional.ofNullable(tokenCookie).map(Cookie::getValue).orElse(null);
		
		if (token != null) {
			jwtService.getUserIfValid(token).ifPresent(user -> {
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null, List.of());
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authToken);
			});
		}
		
		filterChain.doFilter(request, response);
	}
}
