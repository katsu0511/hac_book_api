package com.haradakatsuya190511.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.haradakatsuya190511.entities.Category;
import com.haradakatsuya190511.entities.User;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
	
	@Query("SELECT c FROM Category c WHERE (c.user = :user OR c.user IS NULL) AND c.type = 'INCOME'")
	List<Category> findIncomeByUserOrDefault(@Param("user") User user);
	
	@Query("SELECT c FROM Category c WHERE (c.user = :user OR c.user IS NULL) AND c.type = 'EXPENSE'")
	List<Category> findExpenseByUserOrDefault(@Param("user") User user);
	
	@Query("SELECT c FROM Category c WHERE (c.user = :user OR c.user IS NULL) AND c.parentCategory IS NULL")
	List<Category> findParentCategoriesByUserOrDefault(@Param("user") User user);
	
	Optional<Category> findById(Long id);
}
