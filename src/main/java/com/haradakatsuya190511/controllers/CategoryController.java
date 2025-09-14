package com.haradakatsuya190511.controllers;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haradakatsuya190511.entities.Category;
import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.repositories.CategoryRepository;
import com.haradakatsuya190511.repositories.UserRepository;

@RestController
public class CategoryController {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	CategoryRepository categoryRepository;
	
	@GetMapping("/display/category")
	public ResponseEntity<Map<String, ?>> displayCategory(Principal principal) {
		String email = principal.getName();
		User user = userRepository.findByEmail(email)
	                  .orElseThrow(() -> new UsernameNotFoundException("User not found"));
		List<Category> incomeCategories = categoryRepository.findIncomeByUserOrDefault(user);
		List<Category> expenseCategories = categoryRepository.findExpenseByUserOrDefault(user);
		return ResponseEntity.ok(Map.of("income", incomeCategories, "expense", expenseCategories));
	}
}
