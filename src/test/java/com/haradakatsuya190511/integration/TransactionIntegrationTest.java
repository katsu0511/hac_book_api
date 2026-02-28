package com.haradakatsuya190511.integration;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

import com.jayway.jsonpath.JsonPath;

import jakarta.servlet.http.Cookie;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class TransactionIntegrationTest {
	
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
	void getTransactions_shouldReturnTransactions() throws Exception {
		Cookie cookie = loginAndGetCookie();
		String name = "Category" + UUID.randomUUID();
		MvcResult category = createCategory(cookie, name, "EXPENSE");
		
		Number categoryIdNumber = JsonPath.read(category.getResponse().getContentAsString(), "$.id");
		Long categoryId = categoryIdNumber.longValue();
		
		createTransaction(cookie, categoryId, name, "2026-01-01");
		createTransaction(cookie, categoryId, name, "2026-02-01");
		MvcResult tx = createTransaction(cookie, categoryId, name, "2026-03-01");
		Number txIdNumber = JsonPath.read(tx.getResponse().getContentAsString(), "$.id");
		Long transactionId = txIdNumber.longValue();
		
		mockMvc.perform(get("/transactions?start=2026-03-01&end=2026-03-31").cookie(cookie))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$.length()").value(1))
			.andExpect(jsonPath("$[0].id").value(transactionId))
			.andExpect(jsonPath("$[0].userId").isNumber())
			.andExpect(jsonPath("$[0].categoryId").value(categoryId))
			.andExpect(jsonPath("$[0].categoryName").value(name))
			.andExpect(jsonPath("$[0].categoryType").value("EXPENSE"))
			.andExpect(jsonPath("$[0].amount").value(100.50))
			.andExpect(jsonPath("$[0].currency").value("CAD"))
			.andExpect(jsonPath("$[0].description").value("Lunch"))
			.andExpect(jsonPath("$[0].transactionDate").value("2026-03-01"))
			.andExpect(jsonPath("$[0].createdAt").exists())
			.andExpect(jsonPath("$[0].updatedAt").exists());
	}
	
	@Test
	void getTransactions_withoutAuth_shouldReturn403() throws Exception {
		mockMvc.perform(get("/transactions")).andExpect(status().isForbidden());
	}
	
	@Test
	void getTransaction_shouldReturnTransaction() throws Exception {
		Cookie cookie = loginAndGetCookie();
		String name = "Category" + UUID.randomUUID();
		MvcResult category = createCategory(cookie, name, "EXPENSE");
		
		Number categoryIdNumber = JsonPath.read(category.getResponse().getContentAsString(), "$.id");
		Long categoryId = categoryIdNumber.longValue();
		
		MvcResult tx = createTransaction(cookie, categoryId, name, "2026-01-01");
		Number txIdNumber = JsonPath.read(tx.getResponse().getContentAsString(), "$.id");
		Long transactionId = txIdNumber.longValue();
		
		mockMvc.perform(get("/transactions/{id}", transactionId).cookie(cookie))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(transactionId))
			.andExpect(jsonPath("$.userId").isNumber())
			.andExpect(jsonPath("$.categoryId").value(categoryId))
			.andExpect(jsonPath("$.categoryName").value(name))
			.andExpect(jsonPath("$.categoryType").value("EXPENSE"))
			.andExpect(jsonPath("$.amount").value(100.50))
			.andExpect(jsonPath("$.currency").value("CAD"))
			.andExpect(jsonPath("$.description").value("Lunch"))
			.andExpect(jsonPath("$.transactionDate").value("2026-01-01"))
			.andExpect(jsonPath("$.createdAt").exists())
			.andExpect(jsonPath("$.updatedAt").exists());
	}
	
	@Test
	void getTransaction_ofAnotherUser_shouldReturnNotFound() throws Exception {
		Cookie cookie1 = loginAndGetCookie();
		String name = "Category" + UUID.randomUUID();
		MvcResult category = createCategory(cookie1, name, "EXPENSE");
		
		Number categoryIdNumber = JsonPath.read(category.getResponse().getContentAsString(), "$.id");
		Long categoryId = categoryIdNumber.longValue();
		
		MvcResult tx = createTransaction(cookie1, categoryId, name, "2026-01-01");
		Number txIdNumber = JsonPath.read(tx.getResponse().getContentAsString(), "$.id");
		Long transactionId = txIdNumber.longValue();
		
		Cookie cookie2 = loginAndGetCookie();
		
		mockMvc.perform(get("/transactions/{id}", transactionId).cookie(cookie2))
			.andExpect(status().isNotFound());
	}
	
	@Test
	void getTransaction_withoutAuth_shouldReturn403() throws Exception {
		Cookie cookie = loginAndGetCookie();
		String name = "Category" + UUID.randomUUID();
		MvcResult category = createCategory(cookie, name, "EXPENSE");
		
		Number categoryIdNumber = JsonPath.read(category.getResponse().getContentAsString(), "$.id");
		Long categoryId = categoryIdNumber.longValue();
		
		MvcResult tx = createTransaction(cookie, categoryId, name, "2026-01-01");
		Number txIdNumber = JsonPath.read(tx.getResponse().getContentAsString(), "$.id");
		Long transactionId = txIdNumber.longValue();
		
		mockMvc.perform(get("/transactions/{id}", transactionId)).andExpect(status().isForbidden());
	}
	
	@Test
	void getTransactionForEdit_shouldReturnTransaction() throws Exception {
		Cookie cookie = loginAndGetCookie();
		String name = "Category" + UUID.randomUUID();
		MvcResult category = createCategory(cookie, name, "EXPENSE");
		
		Number categoryIdNumber = JsonPath.read(category.getResponse().getContentAsString(), "$.id");
		Long categoryId = categoryIdNumber.longValue();
		
		MvcResult tx = createTransaction(cookie, categoryId, name, "2026-01-01");
		Number txIdNumber = JsonPath.read(tx.getResponse().getContentAsString(), "$.id");
		Long transactionId = txIdNumber.longValue();
		
		mockMvc.perform(get("/transactions/{id}/edit", transactionId).cookie(cookie))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.transaction.id").value(transactionId))
			.andExpect(jsonPath("$.transaction.userId").isNumber())
			.andExpect(jsonPath("$.transaction.categoryId").value(categoryId))
			.andExpect(jsonPath("$.transaction.categoryName").value(name))
			.andExpect(jsonPath("$.transaction.categoryType").value("EXPENSE"))
			.andExpect(jsonPath("$.transaction.amount").value(100.50))
			.andExpect(jsonPath("$.transaction.currency").value("CAD"))
			.andExpect(jsonPath("$.transaction.description").value("Lunch"))
			.andExpect(jsonPath("$.transaction.transactionDate").value("2026-01-01"))
			.andExpect(jsonPath("$.transaction.createdAt").exists())
			.andExpect(jsonPath("$.transaction.updatedAt").exists())
			.andExpect(jsonPath("$.categories.expense").isArray())
			.andExpect(jsonPath("$.categories.income").isArray());
	}
	
	@Test
	void getTransactionForEdit_ofAnotherUser_shouldReturnNotFound() throws Exception {
		Cookie cookie1 = loginAndGetCookie();
		String name = "Category" + UUID.randomUUID();
		MvcResult category = createCategory(cookie1, name, "EXPENSE");
		
		Number categoryIdNumber = JsonPath.read(category.getResponse().getContentAsString(), "$.id");
		Long categoryId = categoryIdNumber.longValue();
		
		MvcResult tx = createTransaction(cookie1, categoryId, name, "2026-01-01");
		Number txIdNumber = JsonPath.read(tx.getResponse().getContentAsString(), "$.id");
		Long transactionId = txIdNumber.longValue();
		
		Cookie cookie2 = loginAndGetCookie();
		
		mockMvc.perform(get("/transactions/{id}/edit", transactionId).cookie(cookie2))
			.andExpect(status().isNotFound());
	}
	
	@Test
	void getTransactionForEdit_withoutAuth_shouldReturn403() throws Exception {
		Cookie cookie = loginAndGetCookie();
		String name = "Category" + UUID.randomUUID();
		MvcResult category = createCategory(cookie, name, "EXPENSE");
		
		Number categoryIdNumber = JsonPath.read(category.getResponse().getContentAsString(), "$.id");
		Long categoryId = categoryIdNumber.longValue();
		
		MvcResult tx = createTransaction(cookie, categoryId, name, "2026-01-01");
		Number txIdNumber = JsonPath.read(tx.getResponse().getContentAsString(), "$.id");
		Long transactionId = txIdNumber.longValue();
		
		mockMvc.perform(get("/transactions/{id}/edit", transactionId)).andExpect(status().isForbidden());
	}
	
	@Test
	void createTransaction_shouldPersistAndReturnCreated() throws Exception {
		Cookie cookie = loginAndGetCookie();
		String name = "Category" + UUID.randomUUID();
		MvcResult category = createCategory(cookie, name, "EXPENSE");
		
		Number categoryIdNumber = JsonPath.read(category.getResponse().getContentAsString(), "$.id");
		Long categoryId = categoryIdNumber.longValue();
		
		createTransaction(cookie, categoryId, name, "2026-12-31");
	}
	
	@Test
	void createTransaction_withoutAuth_shouldReturn403() throws Exception {
		Cookie cookie = loginAndGetCookie();
		String name = "Category" + UUID.randomUUID();
		MvcResult category = createCategory(cookie, name, "EXPENSE");
		
		Number categoryIdNumber = JsonPath.read(category.getResponse().getContentAsString(), "$.id");
		Long categoryId = categoryIdNumber.longValue();
		
		mockMvc.perform(post("/transactions")
			.contentType(MediaType.APPLICATION_JSON)
			.content("""
				{
					"categoryId": %d,
					"amount": 100.50,
					"currency": "CAD",
					"description": "Lunch",
					"transactionDate": "2026-12-31"
				}
			"""
			.formatted(categoryId)))
			.andExpect(status().isForbidden());
	}
	
	@Test
	void updateTransaction_shouldPersistAndReturnOk() throws Exception {
		Cookie cookie = loginAndGetCookie();
		String name = "Category" + UUID.randomUUID();
		MvcResult category = createCategory(cookie, name, "EXPENSE");
		
		Number categoryIdNumber = JsonPath.read(category.getResponse().getContentAsString(), "$.id");
		Long categoryId = categoryIdNumber.longValue();
		
		MvcResult tx = createTransaction(cookie, categoryId, name, "2026-01-01");
		Number txIdNumber = JsonPath.read(tx.getResponse().getContentAsString(), "$.id");
		Long transactionId = txIdNumber.longValue();
		
		mockMvc.perform(put("/transactions/{id}", transactionId)
			.cookie(cookie)
			.contentType(MediaType.APPLICATION_JSON)
			.content("""
				{
					"id": %d,
					"categoryId": %d,
					"amount": 100.00,
					"currency": "CAD",
					"description": "Lunch",
					"transactionDate": "2026-01-02"
				}
			"""
			.formatted(transactionId, categoryId)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id").value(transactionId))
			.andExpect(jsonPath("$.userId").isNumber())
			.andExpect(jsonPath("$.categoryId").value(categoryId))
			.andExpect(jsonPath("$.categoryName").value(name))
			.andExpect(jsonPath("$.categoryType").value("EXPENSE"))
			.andExpect(jsonPath("$.amount").value(100.00))
			.andExpect(jsonPath("$.currency").value("CAD"))
			.andExpect(jsonPath("$.description").value("Lunch"))
			.andExpect(jsonPath("$.transactionDate").value("2026-01-02"))
			.andExpect(jsonPath("$.createdAt").exists())
			.andExpect(jsonPath("$.updatedAt").exists());
	}
	
	@Test
	void updateTransaction_ofAnotherUser_shouldReturnNotFound() throws Exception {
		Cookie cookie1 = loginAndGetCookie();
		String name = "Category" + UUID.randomUUID();
		MvcResult category = createCategory(cookie1, name, "EXPENSE");
		
		Number categoryIdNumber = JsonPath.read(category.getResponse().getContentAsString(), "$.id");
		Long categoryId = categoryIdNumber.longValue();
		
		MvcResult tx = createTransaction(cookie1, categoryId, name, "2026-01-01");
		Number txIdNumber = JsonPath.read(tx.getResponse().getContentAsString(), "$.id");
		Long transactionId = txIdNumber.longValue();
		
		Cookie cookie2 = loginAndGetCookie();
		
		mockMvc.perform(put("/transactions/{id}", transactionId)
			.cookie(cookie2)
			.contentType(MediaType.APPLICATION_JSON)
			.content("""
				{
					"id": %d,
					"categoryId": %d,
					"amount": 100.00,
					"currency": "CAD",
					"description": "Lunch",
					"transactionDate": "2026-01-02"
				}
			"""
			.formatted(transactionId, categoryId)))
			.andExpect(status().isNotFound());
	}
	
	@Test
	void updateTransaction_withoutAuth_shouldReturn403() throws Exception {
		Cookie cookie = loginAndGetCookie();
		String name = "Category" + UUID.randomUUID();
		MvcResult category = createCategory(cookie, name, "EXPENSE");
		
		Number categoryIdNumber = JsonPath.read(category.getResponse().getContentAsString(), "$.id");
		Long categoryId = categoryIdNumber.longValue();
		
		MvcResult tx = createTransaction(cookie, categoryId, name, "2026-01-01");
		Number txIdNumber = JsonPath.read(tx.getResponse().getContentAsString(), "$.id");
		Long transactionId = txIdNumber.longValue();
		
		mockMvc.perform(put("/transactions/{id}", transactionId)
			.contentType(MediaType.APPLICATION_JSON)
			.content("""
				{
					"categoryId": %d,
					"amount": 100.50,
					"currency": "CAD",
					"description": "Lunch",
					"transactionDate": "2026-12-31"
				}
			"""
			.formatted(categoryId)))
			.andExpect(status().isForbidden());
	}
	
	@Test
	void deleteTransaction_shouldPersistAndReturn204() throws Exception {
		Cookie cookie = loginAndGetCookie();
		String name = "Category" + UUID.randomUUID();
		MvcResult category = createCategory(cookie, name, "EXPENSE");
		
		Number categoryIdNumber = JsonPath.read(category.getResponse().getContentAsString(), "$.id");
		Long categoryId = categoryIdNumber.longValue();
		
		MvcResult tx = createTransaction(cookie, categoryId, name, "2026-01-01");
		Number txIdNumber = JsonPath.read(tx.getResponse().getContentAsString(), "$.id");
		Long transactionId = txIdNumber.longValue();
		
		mockMvc.perform(delete("/transactions/{id}", transactionId).cookie(cookie)).andExpect(status().isNoContent());
		mockMvc.perform(get("/transactions/{id}", transactionId).cookie(cookie)).andExpect(status().isNotFound());
	}
	
	@Test
	void deleteTransaction_ofAnotherUser_shouldReturnNotFound() throws Exception {
		Cookie cookie1 = loginAndGetCookie();
		String name = "Category" + UUID.randomUUID();
		MvcResult category = createCategory(cookie1, name, "EXPENSE");
		
		Number categoryIdNumber = JsonPath.read(category.getResponse().getContentAsString(), "$.id");
		Long categoryId = categoryIdNumber.longValue();
		
		MvcResult tx = createTransaction(cookie1, categoryId, name, "2026-01-01");
		Number txIdNumber = JsonPath.read(tx.getResponse().getContentAsString(), "$.id");
		Long transactionId = txIdNumber.longValue();
		
		Cookie cookie2 = loginAndGetCookie();
		
		mockMvc.perform(delete("/transactions/{id}", transactionId).cookie(cookie2)).andExpect(status().isNotFound());
	}
	
	@Test
	void deleteTransaction_withoutAuth_shouldReturn403() throws Exception {
		Cookie cookie = loginAndGetCookie();
		String name = "Category" + UUID.randomUUID();
		MvcResult category = createCategory(cookie, name, "EXPENSE");
		
		Number categoryIdNumber = JsonPath.read(category.getResponse().getContentAsString(), "$.id");
		Long categoryId = categoryIdNumber.longValue();
		
		MvcResult tx = createTransaction(cookie, categoryId, name, "2026-01-01");
		Number txIdNumber = JsonPath.read(tx.getResponse().getContentAsString(), "$.id");
		Long transactionId = txIdNumber.longValue();
		
		mockMvc.perform(delete("/transactions/{id}", transactionId)).andExpect(status().isForbidden());
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
	
	private MvcResult createCategory(Cookie cookie, String name, String type) throws Exception {
		return mockMvc.perform(post("/categories")
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
	}
	
	private MvcResult createTransaction(Cookie cookie, Long categoryId, String categoryName, String date) throws Exception {
		return mockMvc.perform(post("/transactions")
			.cookie(cookie)
			.contentType(MediaType.APPLICATION_JSON)
			.content("""
				{
					"categoryId": %d,
					"amount": 100.50,
					"currency": "CAD",
					"description": "Lunch",
					"transactionDate": "%s"
				}
			"""
			.formatted(categoryId, date)))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id").isNumber())
			.andExpect(jsonPath("$.userId").isNumber())
			.andExpect(jsonPath("$.categoryId").value(categoryId))
			.andExpect(jsonPath("$.categoryName").value(categoryName))
			.andExpect(jsonPath("$.categoryType").value("EXPENSE"))
			.andExpect(jsonPath("$.amount").value(100.50))
			.andExpect(jsonPath("$.currency").value("CAD"))
			.andExpect(jsonPath("$.description").value("Lunch"))
			.andExpect(jsonPath("$.transactionDate").value(date))
			.andExpect(jsonPath("$.createdAt").doesNotExist())
			.andExpect(jsonPath("$.updatedAt").doesNotExist())
			.andReturn();
	}
}
