package com.haradakatsuya190511.slice.repositories;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.haradakatsuya190511.entities.Category;
import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.enums.CategoryType;
import com.haradakatsuya190511.repositories.CategoryRepository;
import com.haradakatsuya190511.repositories.UserRepository;

import jakarta.persistence.EntityManager;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CategoryRepositoryTest {
	
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
	private CategoryRepository categoryRepository;
	
	@Autowired
	private EntityManager entityManager;
	
	@Test
	void shouldFindOnlyParentCategories() {
		User user = new User("Test User", "test@example.com", "hashed-password");
		userRepository.save(user);
		
		Category parent = new Category(user);
		parent.setName("Food");
		parent.setType(CategoryType.EXPENSE);
		parent.setParentCategory(null);
		categoryRepository.save(parent);
		
		Category child = new Category(user);
		child.setName("Lunch");
		child.setType(CategoryType.EXPENSE);
		child.setParentCategory(parent);
		categoryRepository.save(child);
		
		entityManager.flush();
		entityManager.clear();
		
		List<Category> result = categoryRepository.findParentCategories(user, CategoryType.EXPENSE);
		
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getName()).isEqualTo("Food");
		assertThat(result.get(0).getType()).isEqualTo(CategoryType.EXPENSE);
		assertThat(result.get(0).getParentCategory()).isNull();
		assertThat(result.get(0).getUser().getId()).isEqualTo(user.getId());
	}
	
	@Test
	void shouldFindOnlyChildCategories() {
		User user = new User("Test User", "test@example.com", "hashed-password");
		userRepository.save(user);
		
		Category parent = new Category(user);
		parent.setName("Food");
		parent.setType(CategoryType.EXPENSE);
		parent.setParentCategory(null);
		categoryRepository.save(parent);
		
		Category child = new Category(user);
		child.setName("Lunch");
		child.setType(CategoryType.EXPENSE);
		child.setParentCategory(parent);
		categoryRepository.save(child);
		
		entityManager.flush();
		entityManager.clear();
		
		List<Category> result = categoryRepository.findChildCategories(user, CategoryType.EXPENSE);
		
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getName()).isEqualTo("Lunch");
		assertThat(result.get(0).getType()).isEqualTo(CategoryType.EXPENSE);
		assertThat(result.get(0).getUser().getId()).isEqualTo(user.getId());
		assertThat(result.get(0).getParentCategory()).isNotNull();
		assertThat(result.get(0).getParentCategory().getName()).isEqualTo("Food");
		assertThat(result.get(0).getParentCategory().getType()).isEqualTo(CategoryType.EXPENSE);
		assertThat(result.get(0).getParentCategory().getUser().getId()).isEqualTo(user.getId());
	}
	
	@Test
	void shouldRemoveChildCategories_whenParent_differentUser() {
		User user1 = new User("Test User1", "test1@example.com", "hashed-password");
		userRepository.save(user1);
		User user2 = new User("Test User2", "test2@example.com", "hashed-password");
		userRepository.save(user2);
		
		Category parent1 = new Category(user1);
		parent1.setName("Food");
		parent1.setType(CategoryType.EXPENSE);
		parent1.setParentCategory(null);
		categoryRepository.save(parent1);
		
		Category parent2 = new Category(user2);
		parent2.setName("Supplies");
		parent2.setType(CategoryType.EXPENSE);
		parent2.setParentCategory(null);
		categoryRepository.save(parent2);
		
		Category child1 = new Category(user1);
		child1.setName("Lunch");
		child1.setType(CategoryType.EXPENSE);
		child1.setParentCategory(parent1);
		categoryRepository.save(child1);
		
		Category child2 = new Category(user1);
		child2.setName("Detergent");
		child2.setType(CategoryType.EXPENSE);
		child2.setParentCategory(parent2);
		categoryRepository.save(child2);
		
		entityManager.flush();
		entityManager.clear();
		
		List<Category> result = categoryRepository.findChildCategories(user1, CategoryType.EXPENSE);
		
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getName()).isEqualTo("Lunch");
		assertThat(result.get(0).getType()).isEqualTo(CategoryType.EXPENSE);
		assertThat(result.get(0).getUser().getId()).isEqualTo(user1.getId());
		assertThat(result.get(0).getParentCategory()).isNotNull();
		assertThat(result.get(0).getParentCategory().getName()).isEqualTo("Food");
		assertThat(result.get(0).getParentCategory().getType()).isEqualTo(CategoryType.EXPENSE);
		assertThat(result.get(0).getParentCategory().getUser().getId()).isEqualTo(user1.getId());
	}
	
	@Test
	void shouldInsertDefaultCategories() {
		User user = new User("Test User", "test@example.com", "hashed-password");
		userRepository.save(user);
		
		categoryRepository.insertDefaultCategories(user.getId());
		entityManager.flush();
		entityManager.clear();
		
		List<Category> expenseCategories = categoryRepository.findParentCategories(user, CategoryType.EXPENSE);
		List<Category> incomeCategories = categoryRepository.findParentCategories(user, CategoryType.INCOME);
		
		assertThat(expenseCategories).isNotEmpty();
		assertThat(expenseCategories).hasSize(14);
		assertThat(expenseCategories).allMatch(c -> c.getUser().getId().equals(user.getId()));
		assertThat(incomeCategories).isNotEmpty();
		assertThat(incomeCategories).hasSize(2);
		assertThat(incomeCategories).allMatch(c -> c.getUser().getId().equals(user.getId()));
	}
	
	@Test
	void shouldFindCategoryAndParent() {
		User user = new User("Test User", "test@example.com", "hashed-password");
		userRepository.save(user);
		
		Category parent = new Category(user);
		parent.setName("Food");
		parent.setType(CategoryType.EXPENSE);
		parent.setParentCategory(null);
		categoryRepository.save(parent);
		
		Category child = new Category(user);
		child.setName("Lunch");
		child.setType(CategoryType.EXPENSE);
		child.setParentCategory(parent);
		categoryRepository.save(child);
		
		entityManager.flush();
		entityManager.clear();
		
		Optional<Category> result = categoryRepository.findWithParentByIdAndUserId(child.getId(), user.getId());
		
		assertThat(result).isNotEmpty();
		assertThat(result.get().getName()).isEqualTo("Lunch");
		assertThat(result.get().getType()).isEqualTo(CategoryType.EXPENSE);
		assertThat(result.get().getUser().getId()).isEqualTo(user.getId());
		assertThat(result.get().getParentCategory()).isNotNull();
		assertThat(result.get().getParentCategory().getName()).isEqualTo("Food");
		assertThat(result.get().getParentCategory().getType()).isEqualTo(CategoryType.EXPENSE);
		assertThat(result.get().getParentCategory().getUser().getId()).isEqualTo(user.getId());
	}
}
