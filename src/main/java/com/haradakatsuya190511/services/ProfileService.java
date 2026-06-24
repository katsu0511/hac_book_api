package com.haradakatsuya190511.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.haradakatsuya190511.dtos.profile.SettingResponseDto;
import com.haradakatsuya190511.dtos.profile.UserForProfileResponseDto;
import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.exceptions.SettingNotFoundException;
import com.haradakatsuya190511.exceptions.UserNotFoundException;
import com.haradakatsuya190511.repositories.SettingRepository;
import com.haradakatsuya190511.repositories.UserRepository;

@Service
public class ProfileService {
	
	private final UserRepository userRepository;
	private final SettingRepository settingRepository;
	private final AuthService authService;
	private final PasswordEncoder passwordEncoder;
	
	public ProfileService(UserRepository userRepository, SettingRepository settingRepository, AuthService authService, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.settingRepository = settingRepository;
		this.authService = authService;
		this.passwordEncoder = passwordEncoder;
	}
	
	public UserForProfileResponseDto getUser(User user) {
		return userRepository.findById(user.getId())
			.map(UserForProfileResponseDto::new)
			.orElseThrow(UserNotFoundException::new);
	}
	
	public SettingResponseDto getSetting(User user) {
		return settingRepository.findById(user.getId())
			.filter(s -> s.getUser().getId().equals(user.getId()))
			.map(SettingResponseDto::new)
			.orElseThrow(SettingNotFoundException::new);
	}
	
	public void updateName(Long userId, String name) {
		User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
		user.setName(name);
		userRepository.save(user);
	}
	
	public void updateEmail(Long userId, String email) {
		authService.checkEmailNotExists(email);
		User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
		user.setEmail(email);
		userRepository.save(user);
	}
	
	public void updatePassword(Long userId, String password) {
		String hashedPassword = passwordEncoder.encode(password);
		User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
		user.setPassword(hashedPassword);
		userRepository.save(user);
	}
}
