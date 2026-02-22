package com.haradakatsuya190511.repositories;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.haradakatsuya190511.entities.User;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {
	
	@Container
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18.2").withDatabaseName("test").withUsername("test").withPassword("test");
	
	@DynamicPropertySource
	static void registerProps(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
	}
	
	@Autowired
	private UserRepository userRepository;
	
	@Test
	void findByEmail_shouldReturnUser_whenUserExists() {
		User user = new User("Test User", "test@example.com", "hashed-password");
		userRepository.save(user);
		
		Optional<User> result = userRepository.findByEmailIgnoreCase("test@example.com");
		
		assertThat(result).isPresent();
		assertThat(result.get().getName()).isEqualTo("Test User");
		assertThat(result.get().getEmail()).isEqualTo("test@example.com");
		assertThat(result.get().getPassword()).isEqualTo("hashed-password");
	}
	
	@Test
	void findByEmail_shouldReturnEmpty_whenUserDoesNotExist() {
		User user = new User("Test User", "test@example.com", "hashed-password");
		userRepository.save(user);
		
		Optional<User> result = userRepository.findByEmailIgnoreCase("notfound@example.com");
		assertThat(result).isEmpty();
	}
	
	@Test
	void shouldThrowException_whenEmailDuplicated() {
		User u1 = new User("A", "test@example.com", "hashed-password");
		User u2 = new User("B", "test@example.com", "hashed-password");
		
		userRepository.save(u1);
		
		assertThatThrownBy(() -> userRepository.saveAndFlush(u2)).isInstanceOf(DataIntegrityViolationException.class);
	}
	
	@Test
	void shouldThrowException_whenEmailDuplicated_caseInsensitive() {
		User u1 = new User("A", "test@example.com", "hashed-password");
		User u2 = new User("B", "TEST@example.com", "hashed-password");
		
		userRepository.saveAndFlush(u1);
		
		assertThatThrownBy(() -> userRepository.saveAndFlush(u2)).isInstanceOf(DataIntegrityViolationException.class);
	}
	
	@Test
	void shouldTreatEmailAsCaseInsensitive() {
		User user = new User("A", "TEST@example.com", "hashed-password");
		userRepository.saveAndFlush(user);
		
		Optional<User> result = userRepository.findByEmailIgnoreCase("test@example.com");
		
		assertThat(result).isPresent();
	}
}
