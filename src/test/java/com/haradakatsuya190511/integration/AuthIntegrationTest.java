package com.haradakatsuya190511.integration;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import jakarta.servlet.http.Cookie;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class AuthIntegrationTest {
	
	@Container
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18.2").withDatabaseName("test").withUsername("test").withPassword("test");
	
	@DynamicPropertySource
	static void registerProps(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
	}
	
	@Autowired
	private MockMvc mockMvc;
	
	@Test
	void signup_login_checkAuth_shouldReturnTrue() throws Exception {
		String email = "test" + UUID.randomUUID() + "@example.com";
		signup(email);
		MvcResult loginResult = login(email);
		Cookie cookie = loginResult.getResponse().getCookie("token");
		
		mockMvc.perform(get("/check-auth")
			.cookie(cookie))
			.andExpect(status().isOk())
			.andExpect(content().string("true"));
	}
	
	@Test
	void signup_login_logout_checkAuth_shouldReturnFalse() throws Exception {
		String email = "test" + UUID.randomUUID() + "@example.com";
		signup(email);
		MvcResult loginResult = login(email);
		Cookie cookie = loginResult.getResponse().getCookie("token");
		
		MvcResult logoutResult = mockMvc.perform(post("/logout").cookie(cookie))
			.andExpect(status().isNoContent())
			.andReturn();
		
		Cookie cleared = logoutResult.getResponse().getCookie("token");
		
		mockMvc.perform(get("/check-auth").cookie(cleared))
			.andExpect(status().isOk())
			.andExpect(content().string("false"));
	}
	
	@Test
	void checkAuth_withNoCookie_shouldReturnFalse() throws Exception {
		mockMvc.perform(get("/check-auth"))
			.andExpect(status().isOk())
			.andExpect(content().string("false"));
	}
	
	@Test
	void login_withWrongPassword_shouldReturn401() throws Exception {
		String email = "test" + UUID.randomUUID() + "@example.com";
		signup(email);
		
		mockMvc.perform(post("/login")
			.contentType(MediaType.APPLICATION_JSON)
			.content("""
				{
					"email": "%s",
					"password": "WrongPassword"
				}
			"""
			.formatted(email)))
			.andExpect(status().isUnauthorized());
	}
	
	@Test
	void signup_withDuplicateEmail_shouldReturn409() throws Exception {
		String email = "test" + UUID.randomUUID() + "@example.com";
		signup(email);
		
		mockMvc.perform(post("/signup")
			.contentType(MediaType.APPLICATION_JSON)
			.content("""
				{
					"name": "John",
					"email": "%s",
					"password": "Password@123"
				}
			"""
			.formatted(email)))
			.andExpect(status().isConflict());
	}
	
	@Test
	void checkAuth_withInvalidJwt_shouldReturnFalse() throws Exception {
		Cookie fake = new Cookie("token", "invalid.jwt.token");
		mockMvc.perform(get("/check-auth").cookie(fake))
			.andExpect(status().isOk())
			.andExpect(content().string("false"));
	}
	
	private void signup(String email) throws Exception {
		mockMvc.perform(post("/signup")
			.contentType(MediaType.APPLICATION_JSON)
			.content("""
				{
					"name": "John",
					"email": "%s",
					"password": "Password@123"
				}
			"""
			.formatted(email)))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id").isNumber())
			.andExpect(jsonPath("$.name").value("John"))
			.andExpect(jsonPath("$.email").value(email))
			.andExpect(jsonPath("$.createdAt").isString());
	}
	
	private MvcResult login(String email) throws Exception {
		return mockMvc.perform(post("/login")
			.contentType(MediaType.APPLICATION_JSON)
			.content("""
				{
					"email": "%s",
					"password": "Password@123"
				}
			"""
			.formatted(email)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id").isNumber())
			.andExpect(jsonPath("$.name").value("John"))
			.andExpect(jsonPath("$.email").value(email))
			.andExpect(jsonPath("$.createdAt").isString())
			.andExpect(header().string("Set-Cookie", containsString("HttpOnly")))
			.andExpect(header().string("Set-Cookie", containsString("Path=/")))
			.andExpect(header().string("Set-Cookie", containsString("Max-Age=")))
			.andExpect(header().string("Set-Cookie", containsString("Expires=")))
			.andExpect(header().string("Set-Cookie", containsString("token=")))
			.andReturn();
	}
}
