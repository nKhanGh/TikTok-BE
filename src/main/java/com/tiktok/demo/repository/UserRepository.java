package com.tiktok.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tiktok.demo.entity.User;

import java.util.Optional;
import java.time.LocalDateTime;



@Repository
public interface UserRepository extends JpaRepository<User, String>{
    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameOrEmail(String username, String email);
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email")
    Optional<User> findByEmailWithRoles(String email);
    List<User> findAllByIsVerifiedFalseAndCreatedAtBefore(LocalDateTime date);
}
