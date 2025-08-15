package com.tiktok.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tiktok.demo.entity.InvalidatedToken;
import java.util.List;
import java.util.Date;


@Repository
public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String>{
    List<InvalidatedToken> findByExpiryDateBefore(Date now);
}
