package com.haradakatsuya190511.services;

import org.springframework.stereotype.Service;

import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.exceptions.LoginFailedException;
import com.haradakatsuya190511.repositories.UserRepository;

@Service
public class AuthService {
	
	private final UserRepository repository;
	
	public AuthService(UserRepository repository) {
		this.repository = repository;
	}
	
	public User authenticate(String email, String password) {
		return repository.findByEmail(email)
				.filter(u -> u.getPassword().equals(password))
				.orElseThrow(() -> new LoginFailedException());
    }
}
