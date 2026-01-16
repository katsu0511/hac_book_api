package com.haradakatsuya190511.repositories;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.haradakatsuya190511.entities.Transaction;
import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.enums.CategoryType;

@Repository
public interface DashboardRepository extends JpaRepository<Transaction, Long> {
	
	@Query("""
		SELECT COALESCE(SUM(t.amount), 0)
		FROM Transaction t
		WHERE t.user = :user
		AND t.category.type = :type
		AND t.transactionDate BETWEEN :start AND :end
	""")
	BigDecimal findSumByCategoryTypeInPeriod(
		@Param("user") User user,
		@Param("type") CategoryType type,
		@Param("start") LocalDate start,
		@Param("end") LocalDate end
	);
	
	@Query("""
		SELECT c.id, c.name, c.parentCategory.id, COALESCE(SUM(t.amount), 0)
		FROM Category c
		LEFT JOIN Transaction t
			ON t.category = c
			AND t.user = :user
			AND t.transactionDate BETWEEN :start AND :end
		WHERE (c.user = :user OR c.user IS NULL)
		AND c.type = :type
		GROUP BY c.id, c.name, c.parentCategory.id
		ORDER BY c.id
	""")
	List<Object[]> findBreakdownByCategoryType(
		@Param("user") User user,
		@Param("type") CategoryType type,
		@Param("start") LocalDate start,
		@Param("end") LocalDate end
	);
}
