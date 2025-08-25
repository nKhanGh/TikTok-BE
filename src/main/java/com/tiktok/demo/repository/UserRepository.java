package com.tiktok.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tiktok.demo.entity.User;

import java.util.Optional;
import java.time.LocalDateTime;



@Repository
public interface UserRepository extends JpaRepository<User, String>{
    boolean existsByUsername(String username);

    boolean existsByName(String name);
    boolean existsByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    void deleteByEmail(String email);
    List<User> findAllByIsVerifiedFalseAndCreatedAtBefore(LocalDateTime date);
}
