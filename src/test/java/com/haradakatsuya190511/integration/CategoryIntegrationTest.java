package com.haradakatsuya190511.integration;

import static org.hamcrest.CoreMatchers.containsString;
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
class CategoryIntegrationTest {
	
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
	void getCategories_afterSignup_shouldReturnDefaultCategories() throws Exception {
		Cookie cookie = loginAndGetCookie();
		
		mockMvc.perform(get("/categories").cookie(cookie))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.expense").isArray())
			.andExpect(jsonPath("$.income").isArray())
			.andExpect(jsonPath("$.expense.length()").value(14))
			.andExpect(jsonPath("$.income.length()").value(2));
	}
	
	@Test
	void getCategories_afterSignup_shouldReturnAllCategories() throws Exception {
		Cookie cookie = loginAndGetCookie();
		String name1 = "Category" + UUID.randomUUID();
		createCategory(cookie, name1, "EXPENSE");
		String name2 = "Category" + UUID.randomUUID();
		createCategory(cookie, name2, "INCOME");
		
		mockMvc.perform(get("/categories").cookie(cookie))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.expense").isArray())
			.andExpect(jsonPath("$.income").isArray())
			.andExpect(jsonPath("$.expense.length()").value(15))
			.andExpect(jsonPath("$.income.length()").value(3));
	}
	
	@Test
	void getCategories_withoutAuth_shouldReturn403() throws Exception {
		mockMvc.perform(get("/categories")).andExpect(status().isForbidden());
	}
	
	@Test
	void getParentCategories_afterSignup_shouldReturnDefaultCategories() throws Exception {
		Cookie cookie = loginAndGetCookie();
		
		mockMvc.perform(get("/parent-categories").cookie(cookie))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.expense").isArray())
			.andExpect(jsonPath("$.income").isArray())
			.andExpect(jsonPath("$.expense.length()").value(13))
			.andExpect(jsonPath("$.income.length()").value(1));
	}
	
	@Test
	void getParentCategories_afterSignup_shouldReturnAllCategories() throws Exception {
		Cookie cookie = loginAndGetCookie();
		String name1 = "Category" + UUID.randomUUID();
		createCategory(cookie, name1, "EXPENSE");
		String name2 = "Category" + UUID.randomUUID();
		createCategory(cookie, name2, "INCOME");
		
		mockMvc.perform(get("/parent-categories").cookie(cookie))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.expense").isArray())
			.andExpect(jsonPath("$.income").isArray())
			.andExpect(jsonPath("$.expense.length()").value(14))
			.andExpect(jsonPath("$.income.length()").value(2));
	}
	
	@Test
	void getParentCategories_withoutAuth_shouldReturn403() throws Exception {
		mockMvc.perform(get("/parent-categories")).andExpect(status().isForbidden());
	}
	
	@Test
	void getCategory_shouldReturnCategory() throws Exception {
		Cookie cookie = loginAndGetCookie();
		String name = "Category" + UUID.randomUUID();
		MvcResult result = createCategory(cookie, name, "EXPENSE");
		
		Number idNumber = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
		Long categoryId = idNumber.longValue();
		
		mockMvc.perform(get("/categories/{id}", categoryId).cookie(cookie))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.category.id").isNumber())
			.andExpect(jsonPath("$.category.userId").isNumber())
			.andExpect(jsonPath("$.category.parentId").doesNotExist())
			.andExpect(jsonPath("$.category.name").value(name))
			.andExpect(jsonPath("$.category.type").value("EXPENSE"))
			.andExpect(jsonPath("$.category.description").value(name))
			.andExpect(jsonPath("$.category.active").isBoolean())
			.andExpect(jsonPath("$.parentName").doesNotExist());
	}
	
	@Test
	void getCategory_ofAnotherUser_shouldReturnNotFound() throws Exception {
		Cookie cookie1 = loginAndGetCookie();
		String name = "Category" + UUID.randomUUID();
		MvcResult result = createCategory(cookie1, name, "EXPENSE");
		
		Number idNumber = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
		Long categoryId = idNumber.longValue();
		
		Cookie cookie2 = loginAndGetCookie();
		
		mockMvc.perform(get("/categories/{id}", categoryId).cookie(cookie2))
			.andExpect(status().isNotFound());
	}
	
	@Test
	void getCategory_withoutAuth_shouldReturn403() throws Exception {
		Cookie cookie = loginAndGetCookie();
		String name = "Category" + UUID.randomUUID();
		MvcResult result = createCategory(cookie, name, "EXPENSE");
		
		Number idNumber = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
		Long categoryId = idNumber.longValue();
		
		mockMvc.perform(get("/categories/{id}", categoryId)).andExpect(status().isForbidden());
	}
	
	@Test
	void getCategoryForEdit_shouldReturnCategory() throws Exception {
		Cookie cookie = loginAndGetCookie();
		String name = "Category" + UUID.randomUUID();
		MvcResult result = createCategory(cookie, name, "EXPENSE");
		
		Number idNumber = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
		Long categoryId = idNumber.longValue();
		
		mockMvc.perform(get("/categories/{id}/edit", categoryId).cookie(cookie))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.category.id").isNumber())
			.andExpect(jsonPath("$.category.userId").isNumber())
			.andExpect(jsonPath("$.category.parentId").doesNotExist())
			.andExpect(jsonPath("$.category.name").value(name))
			.andExpect(jsonPath("$.category.type").value("EXPENSE"))
			.andExpect(jsonPath("$.category.description").value(name))
			.andExpect(jsonPath("$.category.active").isBoolean())
			.andExpect(jsonPath("$.categories.expense").isArray())
			.andExpect(jsonPath("$.categories.income").isArray());
	}
	
	@Test
	void getCategoryForEdit_ofAnotherUser_shouldReturnNotFound() throws Exception {
		Cookie cookie1 = loginAndGetCookie();
		String name = "Category" + UUID.randomUUID();
		MvcResult result = createCategory(cookie1, name, "EXPENSE");
		
		Number idNumber = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
		Long categoryId = idNumber.longValue();
		
		Cookie cookie2 = loginAndGetCookie();
		
		mockMvc.perform(get("/categories/{id}/edit", categoryId).cookie(cookie2))
			.andExpect(status().isNotFound());
	}
	
	@Test
	void getCategoryForEdit_withoutAuth_shouldReturn403() throws Exception {
		Cookie cookie = loginAndGetCookie();
		String name = "Category" + UUID.randomUUID();
		MvcResult result = createCategory(cookie, name, "EXPENSE");
		
		Number idNumber = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
		Long categoryId = idNumber.longValue();
		
		mockMvc.perform(get("/categories/{id}/edit", categoryId)).andExpect(status().isForbidden());
	}
	
	@Test
	void createCategory_shouldPersistAndReturnCreated() throws Exception {
		Cookie cookie = loginAndGetCookie();
		String name = "Category" + UUID.randomUUID();
		createCategory(cookie, name, "EXPENSE");
	}
	
	@Test
	void createCategory_withoutAuth_shouldReturn403() throws Exception {
		String name = "Category" + UUID.randomUUID();
		mockMvc.perform(post("/categories")
			.contentType(MediaType.APPLICATION_JSON)
			.content("""
				{
					"parentId": null,
					"name": "%s",
					"type": "EXPENSE",
					"description": "%s"
				}
			"""
			.formatted(name, name)))
			.andExpect(status().isForbidden());
	}
	
	@Test
	void updateCategory_shouldPersistAndReturnOk() throws Exception {
		Cookie cookie = loginAndGetCookie();
		String name = "Category" + UUID.randomUUID();
		MvcResult result = createCategory(cookie, name, "EXPENSE");
		
		Number idNumber = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
		Long categoryId = idNumber.longValue();
		
		mockMvc.perform(put("/categories/{id}", categoryId)
			.cookie(cookie)
			.contentType(MediaType.APPLICATION_JSON)
			.content("""
				{
					"id": %d,
					"parentId": null,
					"name": "%s",
					"type": "EXPENSE",
					"description": "updated"
				}
			"""
			.formatted(categoryId, name)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id").value(categoryId))
			.andExpect(jsonPath("$.userId").isNumber())
			.andExpect(jsonPath("$.parentId").doesNotExist())
			.andExpect(jsonPath("$.name").value(name))
			.andExpect(jsonPath("$.type").value("EXPENSE"))
			.andExpect(jsonPath("$.description").value("updated"))
			.andExpect(jsonPath("$.active").isBoolean());
	}
	
	@Test
	void updateCategory_ofAnotherUser_shouldReturnForbiddenOrNotFound() throws Exception {
		Cookie cookie1 = loginAndGetCookie();
		String name = "Category" + UUID.randomUUID();
		MvcResult result = createCategory(cookie1, name, "EXPENSE");
		
		Number idNumber = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
		Long categoryId = idNumber.longValue();
		
		Cookie cookie2 = loginAndGetCookie();
		
		mockMvc.perform(put("/categories/{id}", categoryId).cookie(cookie2))
			.andExpect(status().isBadRequest());
	}
	
	@Test
	void updateCategory_withoutAuth_shouldReturn403() throws Exception {
		Cookie cookie = loginAndGetCookie();
		String name = "Category" + UUID.randomUUID();
		MvcResult result = createCategory(cookie, name, "EXPENSE");
		
		Number idNumber = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
		Long categoryId = idNumber.longValue();
		
		mockMvc.perform(put("/categories/{id}", categoryId)).andExpect(status().isForbidden());
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
}
