package com.haradakatsuya190511.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.haradakatsuya190511.dtos.auth.SignupRequestDto;
import com.haradakatsuya190511.entities.Setting;
import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.exceptions.EmailAlreadyUsedException;
import com.haradakatsuya190511.exceptions.LoginFailedException;
import com.haradakatsuya190511.repositories.CategoryRepository;
import com.haradakatsuya190511.repositories.SettingRepository;
import com.haradakatsuya190511.repositories.UserRepository;
import com.haradakatsuya190511.utils.JwtUtil;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
	
	@Mock
	UserRepository userRepository;
	
	@Mock
	PasswordEncoder passwordEncoder;
	
	@Mock
	JwtUtil jwtUtil;
	
	@Mock
	SettingRepository settingRepository;
	
	@Mock
	CategoryRepository categoryRepository;
	
	@InjectMocks
	AuthService authService;
	
	@Test
	void authenticate_success() {
		User user = new User("John", "test@example.com", "hashed");
		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
		when(passwordEncoder.matches("password", "hashed")).thenReturn(true);
		User result = authService.authenticate("test@example.com", "password");
		assertEquals(user, result);
	}
	
	@Test
	void authenticate_wrongPassword_throwsException() {
		User user = new User("John", "test@example.com", "hashed");
		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(any(), any())).thenReturn(false);
		assertThrows(LoginFailedException.class, () ->
			authService.authenticate("test@example.com", "wrong")
		);
	}
	
	@Test
	void authenticate_userNotFound_throwsException() {
		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
		assertThrows(LoginFailedException.class, () ->
			authService.authenticate("test@example.com", "password")
		);
	}
	
	@Test
	void generateToken_delegatesToJwtUtil() {
		User user = new User("John", "test@example.com", "hashed");
		when(jwtUtil.generateToken(user)).thenReturn("jwt-token");
		String token = authService.generateToken(user);
		assertEquals("jwt-token", token);
	}
	
	@Test
	void signup_success() {
		SignupRequestDto dto = new SignupRequestDto();
		dto.setName("John");
		dto.setEmail("test@example.com");
		dto.setPassword("password");
		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
		when(passwordEncoder.encode("password")).thenReturn("hashed");
		User savedUser = new User("John", "test@example.com", "hashed");
		when(userRepository.save(any(User.class))).thenReturn(savedUser);
		User result = authService.signup(dto);
		assertEquals("test@example.com", result.getEmail());
		verify(passwordEncoder).encode("password");
		verify(settingRepository).save(any(Setting.class));
		verify(categoryRepository).insertDefaultCategories(savedUser.getId());
	}
	
	@Test
	void signup_emailAlreadyUsed_throwsException() {
		SignupRequestDto dto = new SignupRequestDto();
		dto.setName("John");
		dto.setEmail("test@example.com");
		dto.setPassword("password");
		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new User()));
		assertThrows(EmailAlreadyUsedException.class, () ->
			authService.signup(dto)
		);
	}
	
	@Test
	void checkEmailNotExists_success() {
		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
		assertDoesNotThrow(() -> authService.checkEmailNotExists("test@example.com"));
	}
	
	@Test
	void checkEmailNotExists_whenExists_throwsException() {
		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new User()));
		assertThrows(EmailAlreadyUsedException.class, () ->
			authService.checkEmailNotExists("test@example.com")
		);
	}
}
