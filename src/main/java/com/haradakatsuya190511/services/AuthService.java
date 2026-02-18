package com.haradakatsuya190511.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.haradakatsuya190511.dtos.auth.SignupRequestDto;
import com.haradakatsuya190511.entities.Setting;
import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.exceptions.LoginFailedException;
import com.haradakatsuya190511.exceptions.EmailAlreadyUsedException;
import com.haradakatsuya190511.repositories.CategoryRepository;
import com.haradakatsuya190511.repositories.SettingRepository;
import com.haradakatsuya190511.repositories.UserRepository;
import com.haradakatsuya190511.utils.JwtUtil;

@Service
public class AuthService {
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final SettingRepository settingRepository;
	private final CategoryRepository categoryRepository;
	
	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, SettingRepository settingRepository, CategoryRepository categoryRepository) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtil = jwtUtil;
		this.settingRepository = settingRepository;
		this.categoryRepository = categoryRepository;
	}
	
	public User authenticate(String email, String password) {
		return userRepository.findByEmailIgnoreCase(email)
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
		categoryRepository.insertDefaultCategories(savedUser.getId());
		return savedUser;
	}
	
	public void checkEmailNotExists(String email) {
		userRepository.findByEmailIgnoreCase(email).ifPresent(user -> {
			throw new EmailAlreadyUsedException();
		});
	}
}
