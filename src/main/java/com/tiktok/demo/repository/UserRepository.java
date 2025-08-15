package com.tiktok.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tiktok.demo.entity.User;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, String>{
    boolean existsByUsername(String username);

    boolean existsByName(String name);
    Optional<User> findByUsername(String username);
}
