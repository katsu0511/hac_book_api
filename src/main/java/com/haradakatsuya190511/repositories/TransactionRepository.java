package com.haradakatsuya190511.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.haradakatsuya190511.entities.Transaction;
import com.haradakatsuya190511.entities.User;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
	
	@Query("""
		SELECT t FROM Transaction t
		JOIN FETCH t.category c
		WHERE t.user = :user
		AND c.user = :user
		AND t.transactionDate BETWEEN :start AND :end
		ORDER BY t.transactionDate DESC, t.updatedAt DESC
	""")
	List<Transaction> findAllWithCategoryInPeriod(
		@Param("user") User user,
		@Param("start") LocalDate start,
		@Param("end") LocalDate end
	);
	
	@EntityGraph(attributePaths = {"category"})
	Optional<Transaction> findWithCategoryByUserAndId(User user, Long id);
}
