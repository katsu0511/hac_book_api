package com.haradakatsuya190511.repositories;

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
	Optional<Transaction> findById(Long id);
	List<Transaction> findByUser(User user);
	
	@Query("""
			SELECT COALESCE(SUM(t.amount), 0)
			FROM Transaction t
			JOIN t.category c
			WHERE t.user = :user
				AND c.type = 'INCOME'
				AND t.transactionDate BETWEEN :start AND :end
	""")
	String getTotalIncomeInPeriod(
		@Param("user") User user,
		@Param("start") LocalDate start,
		@Param("end") LocalDate end
	);
	
	@Query("""
			SELECT COALESCE(SUM(t.amount), 0)
			FROM Transaction t
			JOIN t.category c
			WHERE t.user = :user
				AND c.type = 'EXPENSE'
				AND t.transactionDate BETWEEN :start AND :end
	""")
	String getTotalExpenseInPeriod(
		@Param("user") User user,
		@Param("start") LocalDate start,
		@Param("end") LocalDate end
	);
	
	@Query("""
			SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t
			WHERE t.user = :user AND t.category.id = :categoryId
			AND t.transactionDate BETWEEN :start AND :end
	""")
	String findSumByUserAndCategoryAndPeriod(
		@Param("user") User user,
		@Param("categoryId") Long categoryId,
		@Param("start") LocalDate start,
		@Param("end") LocalDate end
	);
}
