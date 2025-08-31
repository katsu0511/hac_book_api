package com.haradakatsuya190511.filters;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

import com.haradakatsuya190511.services.JwtService;
import com.haradakatsuya190511.services.TokenService;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebFilter("/api/*")
public class JwtAuthenticationFilter implements Filter {
	
	@Autowired
	TokenService tokenService;
	
	@Autowired
	JwtService jwtService;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		String token = tokenService.getToken(httpRequest);
		
		if (token != null) {
			try {
				jwtService.checkJwts(token, httpResponse);
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
