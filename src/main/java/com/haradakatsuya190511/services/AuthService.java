package com.haradakatsuya190511.services;

import java.security.Principal;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.haradakatsuya190511.dtos.auth.SignupRequestDto;
import com.haradakatsuya190511.entities.Setting;
import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.exceptions.LoginFailedException;
import com.haradakatsuya190511.exceptions.EmailAlreadyUsedException;
import com.haradakatsuya190511.repositories.SettingRepository;
import com.haradakatsuya190511.repositories.UserRepository;
import com.haradakatsuya190511.utils.JwtUtil;

@Service
public class AuthService {
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final SettingRepository settingRepository;
	
	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, SettingRepository settingRepository) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtil = jwtUtil;
		this.settingRepository = settingRepository;
	}
	
	public User authenticate(String email, String password) {
		return userRepository.findByEmail(email)
				.filter(user -> passwordEncoder.matches(password, user.getPassword()))
				.orElseThrow(LoginFailedException::new);
    }
	
	public String generateToken(User user) {
		return jwtUtil.generateToken(user);
	}
	
	@Transactional
	public User signup(SignupRequestDto signupUser) {
		checkEmailNotExists(signupUser.getEmail());
		String hashedPassword = passwordEncoder.encode(signupUser.getPassword());
		User user = new User(signupUser.getName(), signupUser.getEmail(), hashedPassword);
		User savedUser = userRepository.save(user);
		settingRepository.save(new Setting(savedUser));
		return savedUser;
	}
	
	public void checkEmailNotExists(String email) {
		userRepository.findByEmail(email).ifPresent(user -> {
			throw new EmailAlreadyUsedException();
		});
	}
	
	public User getUser(Principal principal) {
		String email = principal.getName();
		return userRepository.findByEmail(email)
			.orElseThrow(() -> new UsernameNotFoundException("User not found."));
	}
}
