package com.haradakatsuya190511.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.haradakatsuya190511.dtos.LoginRequestDto;
import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.services.AuthService;
import com.haradakatsuya190511.services.JwtService;
import com.haradakatsuya190511.services.TokenService;
import com.haradakatsuya190511.utils.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
public class AuthController {
	
	@Autowired
	AuthService authService;

	@Autowired
	JwtUtil jwtUtil;
	
	@Autowired
	TokenService tokenService;
	
	@Autowired
	JwtService jwtService;
	
	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequestDto loginUser, HttpServletResponse response) {
		User user = authService.authenticate(loginUser.getEmail(), loginUser.getPassword());
		String jwt = jwtUtil.generateToken(user);
		Cookie cookie = new Cookie("token", jwt);
		cookie.setHttpOnly(true);
//		cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setMaxAge(3600);
//		response.addHeader("Set-Cookie", "token=" + jwt + "; Path=/; HttpOnly; Secure; SameSite=Lax");
		response.addHeader("Set-Cookie", "token=" + jwt + "; Path=/; HttpOnly; SameSite=Lax");
		response.addCookie(cookie);
		return ResponseEntity.ok(Map.of("message", "succeeded to login"));
	}
	
	@PostMapping("/logout")
	public ResponseEntity<Map<String, String>> logout(HttpServletRequest request, HttpServletResponse response) {
		Cookie tokenCookie = tokenService.getTokenCookie(request);
		
		if (tokenCookie != null) {
			tokenCookie.setHttpOnly(true);
//			cookie.setSecure(true);
			tokenCookie.setPath("/");
			tokenCookie.setMaxAge(0);
			tokenCookie.setValue("");
			response.addCookie(tokenCookie);
		}
		return ResponseEntity.ok(Map.of("message", "Succeeded to logout"));
	}
	
	@GetMapping("/check-auth")
	public ResponseEntity<Map<String, Boolean>> checkAuth(HttpServletRequest request, HttpServletResponse response) {
		
		Cookie tokenCookie = tokenService.getTokenCookie(request);
		
		if (tokenCookie != null) {
			String token = tokenCookie.getValue();
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
