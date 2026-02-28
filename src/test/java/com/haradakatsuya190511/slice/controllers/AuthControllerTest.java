package com.haradakatsuya190511.slice.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.haradakatsuya190511.controllers.AuthController;
import com.haradakatsuya190511.dtos.auth.SignupRequestDto;
import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.services.AuthService;
import com.haradakatsuya190511.utils.AuthCookieManager;

@WebMvcTest(AuthController.class)
@Import(AuthControllerTest.TestSecurityConfig.class)
class AuthControllerTest {
	
	@Autowired
	MockMvc mockMvc;
	
	@MockitoBean
	AuthService authService;
	
	@MockitoBean
	AuthCookieManager authCookieManager;
	
	@TestConfiguration
	static class TestSecurityConfig {
		@Bean
		SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
			http.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth
					.requestMatchers("/login", "/signup", "/check-auth").permitAll()
					.anyRequest().authenticated()
				)
				.logout(logout -> logout.disable());
			return http.build();
		}
	}
	
	@Test
	@WithMockUser
	void checkAuth_succeedAndShouldReturnTrue() throws Exception {
		mockMvc.perform(get("/check-auth"))
			.andExpect(status().isOk())
			.andExpect(content().string("true"));
	}
	
	@Test
	void checkAuth_failAndShouldReturnFalse_whenUnauthenticated() throws Exception {
		mockMvc.perform(get("/check-auth"))
			.andExpect(status().isOk())
			.andExpect(content().string("false"));
	}
	
	@Test
	void login_succeedAndShouldReturn200() throws Exception {
		User user = new User("John", "test@example.com", "Password@123");
		user.setId(1L);
		
		given(authService.authenticate("test@example.com", "Password@123")).willReturn(user);
		given(authService.generateToken(user)).willReturn("jwt-token");
		
		mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
			.content("""
				{
					"email": "test@example.com",
					"password": "Password@123"
				}
			"""))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id").isNumber())
			.andExpect(jsonPath("$.name").value("John"))
			.andExpect(jsonPath("$.email").value("test@example.com"))
			.andExpect(jsonPath("$.createdAt").doesNotExist());
		
		verify(authService).authenticate(eq("test@example.com"), eq("Password@123"));
		verify(authService).generateToken(user);
		verify(authCookieManager).setToken(any(), eq("jwt-token"));
	}
	
	@Test
	void login_failAndShouldreturn400_whenInvalidRequest() throws Exception {
		mockMvc.perform(post("/login")
			.contentType(MediaType.APPLICATION_JSON)
			.content("{}"))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON));
		
		verify(authService, never()).authenticate(any(), any());
		verify(authService, never()).generateToken(any());
		verify(authCookieManager, never()).setToken(any(), any());
	}
	
	@Test
	@WithMockUser
	void logout_succeedAndShouldReturn204() throws Exception {
		mockMvc.perform(post("/logout")).andExpect(status().isNoContent());
		verify(authCookieManager).clearToken(any());
	}
	
	@Test
	void logout_failAndShouldReturn403_whenUnauthenticated() throws Exception {
		mockMvc.perform(post("/logout")).andExpect(status().isForbidden());
		verify(authCookieManager, never()).clearToken(any());
	}
	
	@Test
	void signup_succeedAndShouldReturn201() throws Exception {		
		User user = new User("John", "test@example.com", "Password@123");
		user.setId(1L);
		
		given(authService.signup(any(SignupRequestDto.class))).willReturn(user);
		given(authService.generateToken(user)).willReturn("jwt-token");
		
		mockMvc.perform(post("/signup")
			.contentType(MediaType.APPLICATION_JSON)
			.content("""
				{
					"name": "John",
					"email": "test@example.com",
					"password": "Password@123"
				}
			"""))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id").isNumber())
			.andExpect(jsonPath("$.name").value("John"))
			.andExpect(jsonPath("$.email").value("test@example.com"))
			.andExpect(jsonPath("$.createdAt").doesNotExist());
		
		verify(authService).signup(any(SignupRequestDto.class));
		verify(authService).generateToken(user);
		verify(authCookieManager).setToken(any(), eq("jwt-token"));
	}
	
	@Test
	void signup_failAndShouldReturn400_whenInvalidRequest() throws Exception {
		mockMvc.perform(post("/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{}"))
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
		
		verify(authService, never()).signup(any());
		verify(authService, never()).generateToken(any());
		verify(authCookieManager, never()).setToken(any(), any());
	}
}
