package com.haradakatsuya190511.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.exceptions.LoginFailedException;
import com.haradakatsuya190511.repositories.UserRepository;
import com.haradakatsuya190511.utils.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthService {
	
	@Autowired
	UserRepository repository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	JwtUtil jwtUtil;
	
	public User authenticate(String email, String password) {
		return repository.findByEmail(email)
				.filter(user -> passwordEncoder.matches(password, user.getPassword()))
				.orElseThrow(LoginFailedException::new);
    }
	
	public void login(HttpServletResponse response, User user) {
		String jwt = jwtUtil.generateToken(user);
		Cookie cookie = new Cookie("token", jwt);
		cookie.setHttpOnly(true);
//		cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setMaxAge(3600);
		response.addCookie(cookie);
	}
}
