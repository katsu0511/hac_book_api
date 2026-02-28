package com.haradakatsuya190511.integration;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
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

import com.jayway.jsonpath.JsonPath;

import jakarta.servlet.http.Cookie;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class DashboardIntegrationTest {
	
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
	void getSummary_shouldReturnCorrectTotals() throws Exception {
		Cookie cookie = loginAndGetCookie();
		String name1 = "Category" + UUID.randomUUID();
		String name2 = "Category" + UUID.randomUUID();
		
		Long expenseCategoryId = createCategoryAndGetId(cookie, name1, "EXPENSE");
		Long incomeCategoryId = createCategoryAndGetId(cookie, name2, "INCOME");
		
		createTransaction(cookie, expenseCategoryId, name1, "2026-01-01", new BigDecimal("100.0"), "EXPENSE");
		createTransaction(cookie, expenseCategoryId, name1, "2026-01-02", new BigDecimal("50.0"), "EXPENSE");
		createTransaction(cookie, incomeCategoryId, name2, "2026-01-03", new BigDecimal("300.0"), "INCOME");
		
		mockMvc.perform(get("/dashboard/summary")
				.param("start", "2026-01-01")
				.param("end", "2026-01-31")
				.cookie(cookie))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.expense").value(150.00))
			.andExpect(jsonPath("$.income").value(300.00))
			.andExpect(jsonPath("$.expenseBreakdown.length()").value(1))
			.andExpect(jsonPath("$.expenseBreakdown[0].categoryId").value(expenseCategoryId))
			.andExpect(jsonPath("$.expenseBreakdown[0].categoryName").value(name1))
			.andExpect(jsonPath("$.expenseBreakdown[0].parentId").doesNotExist())
			.andExpect(jsonPath("$.expenseBreakdown[0].total").value(150.00));
	}
	
	@Test
	void getSummary_withoutAnotherUsersData() throws Exception {
		Cookie cookie1 = loginAndGetCookie();
		String name1 = "Category" + UUID.randomUUID();
		String name2 = "Category" + UUID.randomUUID();
		
		Long expenseCategoryId1 = createCategoryAndGetId(cookie1, name1, "EXPENSE");
		Long incomeCategoryId1 = createCategoryAndGetId(cookie1, name2, "INCOME");
		
		createTransaction(cookie1, expenseCategoryId1, name1, "2026-01-01", new BigDecimal("100.0"), "EXPENSE");
		createTransaction(cookie1, incomeCategoryId1, name2, "2026-01-03", new BigDecimal("300.0"), "INCOME");
		
		Cookie cookie2 = loginAndGetCookie();
		String name3 = "Category" + UUID.randomUUID();
		String name4 = "Category" + UUID.randomUUID();
		
		Long expenseCategoryId2 = createCategoryAndGetId(cookie2, name3, "EXPENSE");
		Long incomeCategoryId2 = createCategoryAndGetId(cookie2, name4, "INCOME");
		
		createTransaction(cookie2, expenseCategoryId2, name3, "2026-01-01", new BigDecimal("150.0"), "EXPENSE");
		createTransaction(cookie2, incomeCategoryId2, name4, "2026-01-03", new BigDecimal("200.0"), "INCOME");
		
		mockMvc.perform(get("/dashboard/summary")
				.param("start", "2026-01-01")
				.param("end", "2026-01-31")
				.cookie(cookie1))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.expense").value(100.00))
			.andExpect(jsonPath("$.income").value(300.00))
			.andExpect(jsonPath("$.expenseBreakdown.length()").value(1))
			.andExpect(jsonPath("$.expenseBreakdown[0].categoryId").value(expenseCategoryId1))
			.andExpect(jsonPath("$.expenseBreakdown[0].categoryName").value(name1))
			.andExpect(jsonPath("$.expenseBreakdown[0].parentId").doesNotExist())
			.andExpect(jsonPath("$.expenseBreakdown[0].total").value(100.00));
	}
	
	@Test
	void getSummary_shouldReturnMultipleExpenseBreakdowns() throws Exception {
		Cookie cookie = loginAndGetCookie();
		String name1 = "Category" + UUID.randomUUID();
		String name2 = "Category" + UUID.randomUUID();
		String name3 = "Category" + UUID.randomUUID();
		
		Long expenseCategoryId1 = createCategoryAndGetId(cookie, name1, "EXPENSE");
		Long expenseCategoryId2 = createCategoryAndGetId(cookie, name2, "EXPENSE");
		Long incomeCategoryId = createCategoryAndGetId(cookie, name3, "INCOME");
		
		createTransaction(cookie, expenseCategoryId1, name1, "2026-01-01", new BigDecimal("100.0"), "EXPENSE");
		createTransaction(cookie, expenseCategoryId1, name1, "2026-01-02", new BigDecimal("50.0"), "EXPENSE");
		createTransaction(cookie, expenseCategoryId2, name2, "2026-01-03", new BigDecimal("200.0"), "EXPENSE");
		createTransaction(cookie, incomeCategoryId, name3, "2026-01-04", new BigDecimal("300.0"), "INCOME");
		
		mockMvc.perform(get("/dashboard/summary")
				.param("start", "2026-01-01")
				.param("end", "2026-01-31")
				.cookie(cookie))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.expense").value(350.00))
			.andExpect(jsonPath("$.income").value(300.00))
			.andExpect(jsonPath("$.expenseBreakdown.length()").value(2))
			.andExpect(jsonPath("$.expenseBreakdown[*].categoryId", containsInAnyOrder(expenseCategoryId1.intValue(), expenseCategoryId2.intValue())))
			.andExpect(jsonPath("$.expenseBreakdown[*].categoryName", containsInAnyOrder(name1, name2)))
			.andExpect(jsonPath("$.expenseBreakdown[*].parentId", everyItem(nullValue())))
			.andExpect(jsonPath("$.expenseBreakdown[*].total", containsInAnyOrder(150.00, 200.00)));
	}
	
	@Test
	void getSummary_whenNoTransactions_shouldReturnZero() throws Exception {
		Cookie cookie = loginAndGetCookie();
		
		mockMvc.perform(get("/dashboard/summary")
				.param("start", "2026-01-01")
				.param("end", "2026-01-31")
				.cookie(cookie))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.expense").value(0))
			.andExpect(jsonPath("$.income").value(0))
			.andExpect(jsonPath("$.expenseBreakdown.length()").value(0));
	}
	
	@Test
	void getSummary_withoutAuth_shouldReturn403() throws Exception {
		Cookie cookie = loginAndGetCookie();
		String name1 = "Category" + UUID.randomUUID();
		String name2 = "Category" + UUID.randomUUID();
		
		Long expenseCategoryId = createCategoryAndGetId(cookie, name1, "EXPENSE");
		Long incomeCategoryId = createCategoryAndGetId(cookie, name2, "INCOME");
		
		createTransaction(cookie, expenseCategoryId, name1, "2026-01-01", new BigDecimal("100.0"), "EXPENSE");
		createTransaction(cookie, incomeCategoryId, name2, "2026-01-03", new BigDecimal("300.0"), "INCOME");
		
		mockMvc.perform(get("/dashboard/summary")
			.param("start", "2026-01-01")
			.param("end", "2026-01-31"))
			.andExpect(status().isForbidden());
	}
	
	private String randomEmail() {
		return "test" + UUID.randomUUID() + "@example.com";
	}
	
	private Cookie loginAndGetCookie() throws Exception {
		String email = randomEmail();
		signup(email);
		MvcResult loginResult = login(email);
		return loginResult.getResponse().getCookie("token");
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
	
	private Long createCategoryAndGetId(Cookie cookie, String name, String type) throws Exception {
		MvcResult result = mockMvc.perform(post("/categories")
			.cookie(cookie)
			.contentType(MediaType.APPLICATION_JSON)
			.content("""
				{
					"parentId": null,
					"name": "%s",
					"type": "%s",
					"description": "%s"
				}
			"""
			.formatted(name, type, name)))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id").isNumber())
			.andExpect(jsonPath("$.userId").isNumber())
			.andExpect(jsonPath("$.parentId").doesNotExist())
			.andExpect(jsonPath("$.name").value(name))
			.andExpect(jsonPath("$.type").value(type))
			.andExpect(jsonPath("$.description").value(name))
			.andExpect(jsonPath("$.active").isBoolean())
			.andReturn();
		
		Number categoryIdNumber = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
		return categoryIdNumber.longValue();
	}
	
	private MvcResult createTransaction(Cookie cookie, Long categoryId, String categoryName, String date, BigDecimal amount, String type) throws Exception {
		return mockMvc.perform(post("/transactions")
			.cookie(cookie)
			.contentType(MediaType.APPLICATION_JSON)
			.content("""
				{
					"categoryId": %d,
					"amount": "%s",
					"currency": "CAD",
					"description": "Lunch",
					"transactionDate": "%s"
				}
			"""
			.formatted(categoryId, amount, date)))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id").isNumber())
			.andExpect(jsonPath("$.userId").isNumber())
			.andExpect(jsonPath("$.categoryId").value(categoryId))
			.andExpect(jsonPath("$.categoryName").value(categoryName))
			.andExpect(jsonPath("$.categoryType").value(type))
			.andExpect(jsonPath("$.amount").value(amount))
			.andExpect(jsonPath("$.currency").value("CAD"))
			.andExpect(jsonPath("$.description").value("Lunch"))
			.andExpect(jsonPath("$.transactionDate").value(date))
			.andExpect(jsonPath("$.createdAt").doesNotExist())
			.andExpect(jsonPath("$.updatedAt").doesNotExist())
			.andReturn();
	}
}
