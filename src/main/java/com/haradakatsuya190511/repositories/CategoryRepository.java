package com.haradakatsuya190511.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.haradakatsuya190511.entities.Category;
import com.haradakatsuya190511.entities.User;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
	
	@Query("""
		SELECT c FROM Category c
		WHERE c.user IS NULL
		AND c.type = 'EXPENSE'
		ORDER BY c.id ASC
	""")
	List<Category> findDefaultExpenseCategories();
	
	@Query("""
		SELECT c FROM Category c
		WHERE c.user IS NULL
		AND c.type = 'INCOME'
		ORDER BY c.id ASC
	""")
	List<Category> findDefaultIncomeCategories();
	
	@Query("""
		SELECT c FROM Category c
		WHERE (c.user = :user OR c.user IS NULL)
		AND c.parentCategory IS NULL
		AND c.type = 'EXPENSE'
		ORDER BY c.id ASC
	""")
	List<Category> findParentExpenseCategories(@Param("user") User user);
	
	@Query("""
		SELECT c FROM Category c
		WHERE (c.user = :user OR c.user IS NULL)
		AND c.parentCategory IS NULL
		AND c.type = 'INCOME'
		ORDER BY c.id ASC
	""")
	List<Category> findParentIncomeCategories(@Param("user") User user);
	
	@Query("""
		SELECT c FROM Category c
		JOIN FETCH c.parentCategory p
		WHERE c.user = :user
		AND p IS NOT NULL
		AND (p.user = :user OR p.user IS NULL)
		AND c.type = 'EXPENSE'
		ORDER BY p.id ASC, c.id ASC
	""")
	List<Category> findChildExpenseCategories(@Param("user") User user);
	
	@Query("""
		SELECT c FROM Category c
		JOIN FETCH c.parentCategory p
		WHERE c.user = :user
		AND p IS NOT NULL
		AND (p.user = :user OR p.user IS NULL)
		AND c.type = 'INCOME'
		ORDER BY p.id ASC, c.id ASC
	""")
	List<Category> findChildIncomeCategories(@Param("user") User user);
	
	@EntityGraph(attributePaths = {"parentCategory"})
	Optional<Category> findWithParentById(Long id);
}
