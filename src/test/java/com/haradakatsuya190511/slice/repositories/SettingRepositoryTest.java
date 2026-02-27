package com.haradakatsuya190511.slice.repositories;

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

import com.haradakatsuya190511.entities.Setting;
import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.repositories.SettingRepository;
import com.haradakatsuya190511.repositories.UserRepository;

import jakarta.persistence.EntityManager;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SettingRepositoryTest {
	
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
	
	@Autowired
	private SettingRepository settingRepository;
	
	@Autowired
	private EntityManager entityManager;
	
	@Test
	void shouldSaveSetting() {
		User user = new User("Test User", "test@example.com", "hashed-password");
		userRepository.save(user);
		
		Setting setting = new Setting(user);
		settingRepository.saveAndFlush(setting);
		
		Optional<Setting> result = settingRepository.findById(user.getId());
		
		assertThat(result).isPresent();
		assertThat(result.get().getLanguage()).isEqualTo("English");
		assertThat(result.get().getCurrency()).isEqualTo("CAD");
	}
	
	@Test
	void shouldThrowException_whenUserNotExists() {
		User fakeUser = entityManager.getReference(User.class, 999L);
		Setting setting = new Setting(fakeUser);
		
		assertThatThrownBy(() -> settingRepository.saveAndFlush(setting)).isInstanceOf(DataIntegrityViolationException.class);
	}
	
	@Test
	void shouldDeleteSetting_whenUserDeleted() {
		User user = new User("Test User", "test@example.com", "hashed-password");
		userRepository.save(user);
		
		Setting setting = new Setting(user);
		settingRepository.saveAndFlush(setting);
		
		entityManager.createQuery("delete from User u where u.id = :id").setParameter("id", user.getId()).executeUpdate();
		userRepository.flush();
		entityManager.clear();
		
		Optional<Setting> result = settingRepository.findById(user.getId());
		
		assertThat(result).isEmpty();
	}
}
