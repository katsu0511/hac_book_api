package com.haradakatsuya190511.services;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.haradakatsuya190511.utils.JwtUtil;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class JwtService {
	
	@Autowired
	JwtUtil jwtUtil;
	
	public void checkJwts(String token, HttpServletResponse response) throws IOException {
		try {
			Jwts.parser()
				.verifyWith(jwtUtil.getSecretKey())
				.build()
				.parseSignedClaims(token);
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
		}
	}
}
