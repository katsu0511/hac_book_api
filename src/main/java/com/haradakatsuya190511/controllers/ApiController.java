package com.haradakatsuya190511.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.haradakatsuya190511.dtos.LoginRequestDto;
import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.repositories.UserRepository;
import com.haradakatsuya190511.services.AuthService;
import com.haradakatsuya190511.utils.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
public class ApiController {

	@Autowired
	UserRepository repository;

	@Autowired
	AuthService authService;

	@Autowired
	JwtUtil jwtUtil;

	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginUser, HttpServletResponse response) {
		User user = authService.authenticate(loginUser.getEmail(), loginUser.getPassword());
		String jwt = jwtUtil.generateToken(user);
		Cookie cookie = new Cookie("token", jwt);
		cookie.setHttpOnly(true);
//		cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setMaxAge(3600);
		response.addCookie(cookie);
		return ResponseEntity.ok(Map.of("message", "succeeded to login"));
	}
}
