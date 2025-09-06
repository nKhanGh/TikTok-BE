package com.tiktok.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tiktok.demo.entity.Hashtag;
import java.util.Optional;


@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, String>{
    Optional<Hashtag> findByTag(String tag);
}
