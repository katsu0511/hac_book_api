package com.haradakatsuya190511.slice.repositories;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
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
import com.haradakatsuya190511.entities.Transaction;
import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.enums.CategoryType;
import com.haradakatsuya190511.repositories.CategoryRepository;
import com.haradakatsuya190511.repositories.TransactionRepository;
import com.haradakatsuya190511.repositories.UserRepository;

import jakarta.persistence.EntityManager;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TransactionRepositoryTest {
	
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
	private TransactionRepository transactionRepository;
	
	@Autowired
	private EntityManager entityManager;
	
	@Test
	void shouldFindTransactionsInPeriodForUserOnly() {
		User user1 = userRepository.saveAndFlush(new User("Test User1", "test1@example.com", "hashed-password"));
		User user2 = userRepository.saveAndFlush(new User("Test User2", "test2@example.com", "hashed-password"));
		
		Category c1 = new Category(user1);
		c1.setName("Food");
		c1.setType(CategoryType.EXPENSE);
		c1.setParentCategory(null);
		categoryRepository.saveAndFlush(c1);
		
		Category c2 = new Category(user2);
		c2.setName("Supplies");
		c2.setType(CategoryType.EXPENSE);
		c2.setParentCategory(null);
		categoryRepository.saveAndFlush(c2);
		
		Transaction t1 = new Transaction(user1);
		t1.setCategory(c1);
		t1.setAmount(new BigDecimal("10.00"));
		t1.setCurrency("CAD");
		t1.setTransactionDate(LocalDate.of(2024, 1, 10));
		transactionRepository.saveAndFlush(t1);
		
		Transaction t2 = new Transaction(user2);
		t2.setCategory(c2);
		t2.setAmount(new BigDecimal("20.00"));
		t2.setCurrency("CAD");
		t2.setTransactionDate(LocalDate.of(2024, 1, 10));
		transactionRepository.saveAndFlush(t2);
		
		entityManager.clear();
		
		List<Transaction> result = transactionRepository.findAllWithCategoryInPeriod(user1, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31));
		
		entityManager.clear();
		
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getUser().getId()).isEqualTo(user1.getId());
		assertThat(result.get(0).getAmount()).isEqualTo("10.00");
		assertThat(result.get(0).getCurrency()).isEqualTo("CAD");
		assertThat(result.get(0).getTransactionDate()).isBetween(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31));
		assertThat(result.get(0).getCategory().getUser().getId()).isEqualTo(user1.getId());
		assertThat(result.get(0).getCategory().getName()).isEqualTo("Food");
		assertThat(result.get(0).getCategory().getType()).isEqualTo(CategoryType.EXPENSE);
	}
	
	@Test
	void shouldFindTransactionsInPeriod_includeStartAndEnd() {
		User user = userRepository.saveAndFlush(new User("Test User", "test@example.com", "hashed-password"));
		
		Category category = new Category(user);
		category.setName("Food");
		category.setType(CategoryType.EXPENSE);
		category.setParentCategory(null);
		categoryRepository.saveAndFlush(category);
		
		Transaction t1 = new Transaction(user);
		t1.setCategory(category);
		t1.setAmount(new BigDecimal("10.00"));
		t1.setCurrency("CAD");
		t1.setTransactionDate(LocalDate.of(2024, 1, 1));
		transactionRepository.saveAndFlush(t1);
		
		Transaction t2 = new Transaction(user);
		t2.setCategory(category);
		t2.setAmount(new BigDecimal("20.00"));
		t2.setCurrency("CAD");
		t2.setTransactionDate(LocalDate.of(2024, 1, 2));
		transactionRepository.saveAndFlush(t2);
		
		Transaction t3 = new Transaction(user);
		t3.setCategory(category);
		t3.setAmount(new BigDecimal("15.00"));
		t3.setCurrency("CAD");
		t3.setTransactionDate(LocalDate.of(2024, 1, 3));
		transactionRepository.saveAndFlush(t3);
		
		Transaction t4 = new Transaction(user);
		t4.setCategory(category);
		t4.setAmount(new BigDecimal("10.00"));
		t4.setCurrency("CAD");
		t4.setTransactionDate(LocalDate.of(2024, 1, 5));
		transactionRepository.saveAndFlush(t4);
		
		Transaction t5 = new Transaction(user);
		t5.setCategory(category);
		t5.setAmount(new BigDecimal("20.00"));
		t5.setCurrency("CAD");
		t5.setTransactionDate(LocalDate.of(2024, 1, 6));
		transactionRepository.saveAndFlush(t5);
		
		entityManager.clear();
		
		List<Transaction> result = transactionRepository.findAllWithCategoryInPeriod(user, LocalDate.of(2024, 1, 2), LocalDate.of(2024, 1, 5));
		
		entityManager.clear();
		
		assertThat(result).hasSize(3);
		assertThat(result).allMatch(t -> t.getUser().getId().equals(user.getId()));
		assertThat(result).allMatch(t -> t.getTransactionDate().isAfter(LocalDate.of(2024, 1, 1)));
		assertThat(result).allMatch(t -> t.getTransactionDate().isBefore(LocalDate.of(2024, 1, 6)));
		assertThat(result).allMatch(t -> t.getCategory().getUser().getId().equals(user.getId()));
		assertThat(result).allMatch(t -> t.getCategory().getName().equals("Food"));
		assertThat(result).allMatch(t -> t.getCategory().getType().equals(CategoryType.EXPENSE));
	}
	
	@Test
	void shouldFindTransactionWithCategory() {
		User user = userRepository.saveAndFlush(new User("Test User", "test@example.com", "hashed-password"));
		
		Category category = new Category(user);
		category.setName("Food");
		category.setType(CategoryType.EXPENSE);
		category.setParentCategory(null);
		categoryRepository.saveAndFlush(category);
		
		Transaction tx = new Transaction(user);
		tx.setCategory(category);
		tx.setAmount(new BigDecimal("10.00"));
		tx.setCurrency("CAD");
		tx.setTransactionDate(LocalDate.of(2024, 1, 1));
		transactionRepository.saveAndFlush(tx);
		
		entityManager.clear();
		
		Optional<Transaction> result = transactionRepository.findWithCategoryByUserAndId(user, tx.getId());
		
		entityManager.clear();
		
		assertThat(result.get().getUser().getId()).isEqualTo(user.getId());
		assertThat(result.get().getAmount()).isEqualTo("10.00");
		assertThat(result.get().getCurrency()).isEqualTo("CAD");
		assertThat(result.get().getTransactionDate()).isEqualTo(LocalDate.of(2024, 1, 1));
		assertThat(result.get().getCategory().getUser().getId()).isEqualTo(user.getId());
		assertThat(result.get().getCategory().getName()).isEqualTo("Food");
		assertThat(result.get().getCategory().getType()).isEqualTo(CategoryType.EXPENSE);
	}
	
	@Test
	void shouldNotReturnTransactionOfAnotherUser() {
		User user1 = userRepository.saveAndFlush(new User("Test User1", "test1@example.com", "hashed-password"));
		User user2 = userRepository.saveAndFlush(new User("Test User2", "test2@example.com", "hashed-password"));
		
		Category category = new Category(user1);
		category.setName("Food");
		category.setType(CategoryType.EXPENSE);
		category.setParentCategory(null);
		categoryRepository.saveAndFlush(category);
		
		Transaction tx = new Transaction(user1);
		tx.setCategory(category);
		tx.setAmount(new BigDecimal("10.00"));
		tx.setCurrency("CAD");
		tx.setTransactionDate(LocalDate.of(2024, 1, 1));
		transactionRepository.saveAndFlush(tx);
		
		entityManager.clear();
		
		Optional<Transaction> result = transactionRepository.findWithCategoryByUserAndId(user2, tx.getId());
		
		assertThat(result).isEmpty();
	}
}
