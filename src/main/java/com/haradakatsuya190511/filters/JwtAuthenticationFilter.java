package com.haradakatsuya190511.filters;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

import com.haradakatsuya190511.utils.JwtUtil;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebFilter("/api/*")
public class JwtAuthenticationFilter implements Filter {
	
	@Autowired
	JwtUtil jwtUtil;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
	    HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		String token = null;
		Cookie[] cookies = httpRequest.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("token")) {
					token = cookie.getValue();
					break;
				}
			}
		}
		
		if (token != null) {
			try {
				Jwts.parser()
					.verifyWith(jwtUtil.getSecretKey())
					.build()
					.parseSignedClaims(token);
				chain.doFilter(request, response);
			} catch (Exception e) {
				httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
			}
		} else {
			httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is missing");
		}
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {}
	
	@Override
	public void destroy() {}
}
