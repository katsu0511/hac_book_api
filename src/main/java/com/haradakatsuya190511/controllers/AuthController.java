package com.haradakatsuya190511.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.haradakatsuya190511.dtos.LoginRequestDto;
import com.haradakatsuya190511.dtos.SignupRequestDto;
import com.haradakatsuya190511.dtos.UserResponseDto;
import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.services.AuthService;
import com.haradakatsuya190511.services.TokenService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
public class AuthController {
	
	@Autowired
	AuthService authService;
	
	@Autowired
	TokenService tokenService;
	
	@GetMapping("/check-auth")
	public ResponseEntity<Map<String, Boolean>> checkAuth(HttpServletRequest request, HttpServletResponse response) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean authenticated = authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken);
		return ResponseEntity.ok(Map.of("authenticated", authenticated));
	}
	
	@PostMapping("/login")
	public ResponseEntity<UserResponseDto> login(@Valid @RequestBody LoginRequestDto loginUser, HttpServletResponse response) {
		User user = authService.authenticate(loginUser.getEmail(), loginUser.getPassword());
		authService.login(user, response);
		return ResponseEntity.ok(new UserResponseDto(user));
	}
	
	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletResponse response) {
		authService.logout(response);
		return ResponseEntity.noContent().build();
	}
	
	@PostMapping("/signup")
	public ResponseEntity<UserResponseDto> signup(@Valid @RequestBody SignupRequestDto signupUser, HttpServletResponse response) {
		User user = authService.signup(signupUser);
		authService.login(user, response);
		return ResponseEntity.ok(new UserResponseDto(user));
	}
}
