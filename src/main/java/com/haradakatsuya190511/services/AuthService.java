package com.haradakatsuya190511.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.exceptions.LoginFailedException;
import com.haradakatsuya190511.repositories.UserRepository;

@Service
public class AuthService {
	
	@Autowired
	UserRepository repository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	public User authenticate(String email, String password) {
		return repository.findByEmail(email)
				.filter(user -> passwordEncoder.matches(password, user.getPassword()))
				.orElseThrow(LoginFailedException::new);
    }
}
