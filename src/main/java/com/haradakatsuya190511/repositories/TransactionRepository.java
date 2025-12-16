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

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	@Query("""
		SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t
		WHERE t.user = :user
		AND t.category.type = 'EXPENSE'
		AND t.transactionDate BETWEEN :start AND :end
	""")
	String findTotalExpenseInPeriod(
		@Param("user") User user,
		@Param("start") LocalDate start,
		@Param("end") LocalDate end
	);

	@Query("""
		SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t
		WHERE t.user = :user
		AND t.category.type = 'INCOME'
		AND t.transactionDate BETWEEN :start AND :end
	""")
	String findTotalIncomeInPeriod(
		@Param("user") User user,
		@Param("start") LocalDate start,
		@Param("end") LocalDate end
	);

	@Query("""
		SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t
		WHERE t.user = :user
		AND (t.category.id = :categoryId OR t.category.parentCategory.id = :categoryId)
		AND t.transactionDate BETWEEN :start AND :end
	""")
	String findSumByCategoryAndPeriod(
		@Param("user") User user,
		@Param("categoryId") Long categoryId,
		@Param("start") LocalDate start,
		@Param("end") LocalDate end
	);

	@Query("""
		SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t
		JOIN t.category c
		LEFT JOIN c.parentCategory parent
		WHERE t.user = :user
		AND c.user = :user
		AND (parent IS NULL OR parent.user = :user)
		AND c.type = 'EXPENSE'
		AND t.transactionDate BETWEEN :start AND :end
	""")
	BigDecimal findTotalExpenseByCategoryInPeriod(
		@Param("user") User user,
		@Param("start") LocalDate start,
		@Param("end") LocalDate end
	);

	@Query("""
		SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t
		JOIN t.category c
		LEFT JOIN c.parentCategory parent
		WHERE t.user = :user
		AND c.user = :user
		AND (parent IS NULL OR parent.user = :user)
		AND c.type = 'INCOME'
		AND t.transactionDate BETWEEN :start AND :end
	""")
	BigDecimal findTotalIncomeByCategoryInPeriod(
		@Param("user") User user,
		@Param("start") LocalDate start,
		@Param("end") LocalDate end
	);

	Optional<Transaction> findById(Long id);
	List<Transaction> findByUser(User user);
}
