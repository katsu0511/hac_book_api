package com.haradakatsuya190511.repositories;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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

import jakarta.persistence.EntityManager;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DashboardRepositoryTest {
	
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
	private DashboardRepository dashboardRepository;
	
	@Autowired
	private EntityManager entityManager;
	
	@Test
	void shouldReturnExpenseSumWithinPeriod() {
		User user = userRepository.saveAndFlush(new User("Test User", "test@example.com", "hashed-password"));
		
		Category category = new Category(user);
		category.setName("Food");
		category.setType(CategoryType.EXPENSE);
		category.setParentCategory(null);
		categoryRepository.saveAndFlush(category);
		
		Transaction t1 = new Transaction(user);
		t1.setCategory(category);
		t1.setAmount(new BigDecimal("1000"));
		t1.setCurrency("CAD");
		t1.setTransactionDate(LocalDate.of(2026, 1, 1));
		transactionRepository.saveAndFlush(t1);
		
		Transaction t2 = new Transaction(user);
		t2.setCategory(category);
		t2.setAmount(new BigDecimal("500"));
		t2.setCurrency("CAD");
		t2.setTransactionDate(LocalDate.of(2026, 1, 10));
		transactionRepository.saveAndFlush(t2);
		
		Transaction t3 = new Transaction(user);
		t3.setCategory(category);
		t3.setAmount(new BigDecimal("700"));
		t3.setCurrency("CAD");
		t3.setTransactionDate(LocalDate.of(2026, 1, 11));
		transactionRepository.saveAndFlush(t3);
		
		entityManager.clear();
		
		BigDecimal result = dashboardRepository.findSumByCategoryTypeInPeriod(user, CategoryType.EXPENSE, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 10));
		
		entityManager.clear();
		
		assertThat(result).isEqualByComparingTo("1500");
	}
	
	@Test
	void shouldReturnIncomeSumWithinPeriod() {
		User user = userRepository.saveAndFlush(new User("Test User", "test@example.com", "hashed-password"));
		
		Category category = new Category(user);
		category.setName("Salary");
		category.setType(CategoryType.INCOME);
		category.setParentCategory(null);
		categoryRepository.saveAndFlush(category);
		
		Transaction t1 = new Transaction(user);
		t1.setCategory(category);
		t1.setAmount(new BigDecimal("5000"));
		t1.setCurrency("CAD");
		t1.setTransactionDate(LocalDate.of(2026, 1, 1));
		transactionRepository.saveAndFlush(t1);
		
		Transaction t2 = new Transaction(user);
		t2.setCategory(category);
		t2.setAmount(new BigDecimal("500"));
		t2.setCurrency("CAD");
		t2.setTransactionDate(LocalDate.of(2026, 1, 10));
		transactionRepository.saveAndFlush(t2);
		
		Transaction t3 = new Transaction(user);
		t3.setCategory(category);
		t3.setAmount(new BigDecimal("700"));
		t3.setCurrency("CAD");
		t3.setTransactionDate(LocalDate.of(2026, 1, 11));
		transactionRepository.saveAndFlush(t3);
		
		entityManager.clear();
		
		BigDecimal result = dashboardRepository.findSumByCategoryTypeInPeriod(user, CategoryType.INCOME, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 10));
		
		entityManager.clear();
		
		assertThat(result).isEqualByComparingTo("5500");
	}
	
	@Test
	void shouldReturnZero_whenNoTransactionWithinPeriod() {
		User user = userRepository.saveAndFlush(new User("Test User", "test@example.com", "hashed-password"));
		
		Category category = new Category(user);
		category.setName("Food");
		category.setType(CategoryType.EXPENSE);
		category.setParentCategory(null);
		categoryRepository.saveAndFlush(category);
		
		Transaction t1 = new Transaction(user);
		t1.setCategory(category);
		t1.setAmount(new BigDecimal("1000"));
		t1.setCurrency("CAD");
		t1.setTransactionDate(LocalDate.of(2026, 1, 1));
		transactionRepository.saveAndFlush(t1);
		
		Transaction t2 = new Transaction(user);
		t2.setCategory(category);
		t2.setAmount(new BigDecimal("500"));
		t2.setCurrency("CAD");
		t2.setTransactionDate(LocalDate.of(2026, 1, 2));
		transactionRepository.saveAndFlush(t2);
		
		Transaction t3 = new Transaction(user);
		t3.setCategory(category);
		t3.setAmount(new BigDecimal("700"));
		t3.setCurrency("CAD");
		t3.setTransactionDate(LocalDate.of(2026, 1, 11));
		transactionRepository.saveAndFlush(t3);
		
		entityManager.clear();
		
		BigDecimal result = dashboardRepository.findSumByCategoryTypeInPeriod(user, CategoryType.EXPENSE, LocalDate.of(2026, 1, 3), LocalDate.of(2026, 1, 10));
		
		entityManager.clear();
		
		assertThat(result).isEqualByComparingTo("0");
	}
	
	@Test
	void shouldReturnBreakdownWithZeroForEmptyCategories() {
		User user = userRepository.saveAndFlush(new User("Test User", "test@example.com", "hashed-password"));
		
		Category food = new Category(user);
		food.setName("Food");
		food.setType(CategoryType.EXPENSE);
		food.setParentCategory(null);
		categoryRepository.saveAndFlush(food);
		
		Category rent = new Category(user);
		rent.setName("Rent");
		rent.setType(CategoryType.EXPENSE);
		rent.setParentCategory(null);
		categoryRepository.saveAndFlush(rent);
		
		Transaction tx = new Transaction(user);
		tx.setCategory(food);
		tx.setAmount(new BigDecimal("200"));
		tx.setCurrency("CAD");
		tx.setTransactionDate(LocalDate.now());
		transactionRepository.saveAndFlush(tx);
		
		entityManager.clear();
		
		List<Object[]> result = dashboardRepository.findBreakdownByCategoryType(user, CategoryType.EXPENSE, LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
		
		entityManager.clear();
		
		assertThat(result).hasSize(2);
		
		Object[] foodRow = result.get(0);
		Object[] rentRow = result.get(1);
		
		assertThat((Long) foodRow[0]).isEqualTo(food.getId());
		assertThat(foodRow[1].toString()).isEqualTo("Food");
		assertThat((Long) foodRow[2]).isEqualTo(null);
		assertThat((BigDecimal) foodRow[3]).isEqualByComparingTo(new BigDecimal("200"));
		assertThat((Long) rentRow[0]).isEqualTo(rent.getId());
		assertThat(rentRow[1].toString()).isEqualTo("Rent");
		assertThat((Long) rentRow[2]).isEqualTo(null);
		assertThat((BigDecimal) rentRow[3]).isEqualByComparingTo(new BigDecimal("0"));
	}
	
	@Test
	void shouldReturnBreakdownWithParentCategory() {
		User user = userRepository.saveAndFlush(new User("Test User", "test@example.com", "hashed-password"));
		
		Category food = new Category(user);
		food.setName("Food");
		food.setType(CategoryType.EXPENSE);
		food.setParentCategory(null);
		categoryRepository.saveAndFlush(food);
		
		Category groceries = new Category(user);
		groceries.setName("Groceries");
		groceries.setType(CategoryType.EXPENSE);
		groceries.setParentCategory(food);
		categoryRepository.saveAndFlush(groceries);
		
		Transaction t1 = new Transaction(user);
		t1.setCategory(food);
		t1.setAmount(new BigDecimal("200"));
		t1.setCurrency("CAD");
		t1.setTransactionDate(LocalDate.now());
		transactionRepository.saveAndFlush(t1);
		
		Transaction t2 = new Transaction(user);
		t2.setCategory(groceries);
		t2.setAmount(new BigDecimal("100"));
		t2.setCurrency("CAD");
		t2.setTransactionDate(LocalDate.now());
		transactionRepository.saveAndFlush(t2);
		
		entityManager.clear();
		
		List<Object[]> result = dashboardRepository.findBreakdownByCategoryType(user, CategoryType.EXPENSE, LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
		
		entityManager.clear();
		
		assertThat(result).hasSize(2);
		
		Object[] foodRow = result.get(0);
		Object[] groceriesRow = result.get(1);
		
		assertThat((Long) foodRow[0]).isEqualTo(food.getId());
		assertThat(foodRow[1].toString()).isEqualTo("Food");
		assertThat((Long) foodRow[2]).isEqualTo(null);
		assertThat((BigDecimal) foodRow[3]).isEqualByComparingTo(new BigDecimal("200"));
		assertThat((Long) groceriesRow[0]).isEqualTo(groceries.getId());
		assertThat(groceriesRow[1].toString()).isEqualTo("Groceries");
		assertThat((Long) groceriesRow[2]).isEqualTo(groceries.getParentCategory().getId());
		assertThat((BigDecimal) groceriesRow[3]).isEqualByComparingTo(new BigDecimal("100"));
	}
	
	@Test
	void shouldNotReturnBreakdownOfAnotherUser() {
		User user1 = userRepository.saveAndFlush(new User("Test User1", "test1@example.com", "hashed-password"));
		User user2 = userRepository.saveAndFlush(new User("Test User2", "test2@example.com", "hashed-password"));
		
		Category food = new Category(user1);
		food.setName("Food");
		food.setType(CategoryType.EXPENSE);
		food.setParentCategory(null);
		categoryRepository.saveAndFlush(food);
		
		Transaction tx = new Transaction(user1);
		tx.setCategory(food);
		tx.setAmount(new BigDecimal("200"));
		tx.setCurrency("CAD");
		tx.setTransactionDate(LocalDate.now());
		transactionRepository.saveAndFlush(tx);
		
		entityManager.clear();
		
		List<Object[]> result = dashboardRepository.findBreakdownByCategoryType(user2, CategoryType.EXPENSE, LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
		
		entityManager.clear();
		
		assertThat(result).hasSize(0);
		assertThat(result).isEmpty();
	}
}
