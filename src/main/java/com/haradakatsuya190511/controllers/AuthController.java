package com.haradakatsuya190511.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haradakatsuya190511.services.JwtService;
import com.haradakatsuya190511.services.TokenService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class AuthController {
	
	@Autowired
	TokenService tokenService;
	
	@Autowired
	JwtService jwtService;
	
	@GetMapping("/check-auth")
	public ResponseEntity<Map<String, Boolean>> checkAuth(HttpServletRequest request, HttpServletResponse response) {
		
		String token = tokenService.getToken(request);
		
		if (token != null) {
			try {
				jwtService.checkJwts(token, response);
				return ResponseEntity.ok(Map.of("authenticated", true));
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("authenticated", false));
			}
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("authenticated", false));
		}
	}
}
