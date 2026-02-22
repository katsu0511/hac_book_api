package com.haradakatsuya190511.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.haradakatsuya190511.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
	@Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)")
	Optional<User> findByEmailIgnoreCase(@Param("email") String email);
}
