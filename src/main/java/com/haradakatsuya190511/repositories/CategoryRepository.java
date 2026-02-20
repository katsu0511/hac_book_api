package com.haradakatsuya190511.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.haradakatsuya190511.entities.Category;
import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.enums.CategoryType;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
	
	@Query("""
		SELECT c FROM Category c
		WHERE c.user = :user
		AND c.parentCategory IS NULL
		AND c.type = :type
		ORDER BY c.id ASC
	""")
	List<Category> findParentCategories(@Param("user") User user, @Param("type") CategoryType type);
	
	@Query("""
		SELECT c FROM Category c
		JOIN FETCH c.parentCategory p
		WHERE c.user = :user
		AND p.user = :user
		AND c.type = :type
		ORDER BY c.parentCategory ASC, c.id ASC
	""")
	List<Category> findChildCategories(@Param("user") User user, @Param("type") CategoryType type);
	
	@Modifying
	@Query(value = """
		INSERT INTO categories (user_id, name, type, description)
		SELECT :userId, name, type, NULL
		FROM category_templates
		ORDER BY display_order
	""", nativeQuery = true)
	void insertDefaultCategories(@Param("userId") Long userId);
	
	@EntityGraph(attributePaths = {"parentCategory"})
	Optional<Category> findWithParentByIdAndUserId(Long id, Long userId);
}
