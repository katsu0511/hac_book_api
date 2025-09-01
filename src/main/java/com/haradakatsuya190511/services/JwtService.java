package com.haradakatsuya190511.services;

import java.io.IOException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.haradakatsuya190511.utils.JwtUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
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
	
	public boolean validateToken(String token) {
		try {
			Jws<Claims> claimsJws = Jwts.parser()
				.verifyWith(jwtUtil.getSecretKey())
				.build()
				.parseSignedClaims(token);
			Claims claims = claimsJws.getPayload();
			Date expiration = claims.getExpiration();
			return expiration != null && expiration.after(new Date());
		} catch (Exception e) {
			return false;
		}
	}
	
	public String extractEmail(String token) {
		Claims claims = Jwts.parser()
			.verifyWith(jwtUtil.getSecretKey())
			.build()
			.parseSignedClaims(token)
			.getPayload();
		return claims.getSubject();
	}
}
