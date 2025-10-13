package com.haradakatsuya190511.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.haradakatsuya190511.entities.Transaction;
import com.haradakatsuya190511.entities.User;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
	Optional<Transaction> findById(Long id);
	List<Transaction> findByUser(User user);
}
