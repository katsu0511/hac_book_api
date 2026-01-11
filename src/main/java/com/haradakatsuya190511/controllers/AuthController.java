package com.haradakatsuya190511.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.haradakatsuya190511.dtos.auth.LoginRequestDto;
import com.haradakatsuya190511.dtos.auth.SignupRequestDto;
import com.haradakatsuya190511.dtos.auth.UserResponseDto;
import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.services.AuthService;
import com.haradakatsuya190511.utils.AuthCookieManager;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
public class AuthController {
	
	private final AuthService authService;
	private final AuthCookieManager cookieManager;
	
	public AuthController(AuthService authService, AuthCookieManager cookieManager) {
		this.authService = authService;
		this.cookieManager = cookieManager;
	}
	
	@GetMapping("/check-auth")
	public ResponseEntity<Boolean> checkAuth(Authentication authentication) {
		boolean authenticated = authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken);
		return ResponseEntity.ok(authenticated);
	}
	
	@PostMapping("/login")
	public ResponseEntity<UserResponseDto> login(@Valid @RequestBody LoginRequestDto loginUser, HttpServletResponse response) {
		User user = authService.authenticate(loginUser.getEmail(), loginUser.getPassword());
		String jwt = authService.generateToken(user);
		cookieManager.setToken(response, jwt);
		return ResponseEntity.ok(new UserResponseDto(user));
	}
	
	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletResponse response) {
		cookieManager.clearToken(response);
		return ResponseEntity.noContent().build();
	}
	
	@PostMapping("/signup")
	public ResponseEntity<UserResponseDto> signup(@Valid @RequestBody SignupRequestDto signupUser, HttpServletResponse response) {
		User user = authService.signup(signupUser);
		String jwt = authService.generateToken(user);
		cookieManager.setToken(response, jwt);
		return ResponseEntity.ok(new UserResponseDto(user));
	}
}
