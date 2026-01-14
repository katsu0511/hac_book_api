package com.haradakatsuya190511.repositories;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.haradakatsuya190511.entities.Transaction;
import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.enums.CategoryType;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	@Query("""
		SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t
		WHERE t.user = :user
		AND t.category.type = :type
		AND t.transactionDate BETWEEN :start AND :end
	""")
	BigDecimal findSumByCategoryTypeAndPeriod(
		@Param("user") User user,
		@Param("type") CategoryType type,
		@Param("start") LocalDate start,
		@Param("end") LocalDate end
	);
	
	@Query("""
		SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t
		WHERE t.user = :user
		AND (t.category.id = :categoryId OR t.category.parentCategory.id = :categoryId)
		AND t.transactionDate BETWEEN :start AND :end
	""")
	BigDecimal findSumByCategoryAndPeriod(
		@Param("user") User user,
		@Param("categoryId") Long categoryId,
		@Param("start") LocalDate start,
		@Param("end") LocalDate end
	);
	
	@Query("""
		SELECT t FROM Transaction t
		JOIN FETCH t.category
		WHERE t.user = :user
		AND t.transactionDate BETWEEN :start AND :end
	""")
	List<Transaction> findAllWithCategoryInPeriod(
		@Param("user") User user,
		@Param("start") LocalDate start,
		@Param("end") LocalDate end
	);
	
	@Query("""
		SELECT t FROM Transaction t
		JOIN FETCH t.category
		WHERE t.id = :id AND t.user = :user
	""")
	Optional<Transaction> findByIdWithCategory(@Param("user") User user, @Param("id") Long id);
}
