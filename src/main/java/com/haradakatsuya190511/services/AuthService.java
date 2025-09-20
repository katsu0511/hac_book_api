package com.haradakatsuya190511.services;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.haradakatsuya190511.dtos.SignupRequestDto;
import com.haradakatsuya190511.entities.Setting;
import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.exceptions.LoginFailedException;
import com.haradakatsuya190511.exceptions.SignupFailedException;
import com.haradakatsuya190511.repositories.SettingRepository;
import com.haradakatsuya190511.repositories.UserRepository;
import com.haradakatsuya190511.utils.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthService {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	JwtUtil jwtUtil;
	
	@Autowired
	SettingRepository settingRepository;
	
	public User authenticate(String email, String password) {
		return userRepository.findByEmail(email)
				.filter(user -> passwordEncoder.matches(password, user.getPassword()))
				.orElseThrow(LoginFailedException::new);
    }
	
	public void checkEmailNotExists(String email) {
		userRepository.findByEmail(email).ifPresent(user -> {
			throw new SignupFailedException();
		});
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
	
	@Transactional
	public User signup(SignupRequestDto signupUser) {
		checkEmailNotExists(signupUser.getEmail());
		String hashedPassword = passwordEncoder.encode(signupUser.getPassword());
		User user = new User(signupUser.getName(), signupUser.getEmail(), hashedPassword);
		User savedUser = userRepository.save(user);
		settingRepository.save(new Setting(savedUser));
		return savedUser;
	}
	
	public User getUser(Principal principal) {
		String email = principal.getName();
		User user = userRepository.findByEmail(email)
	                  .orElseThrow(() -> new UsernameNotFoundException("User not found"));
		return user;
	}
}
