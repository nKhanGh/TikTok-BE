package com.tiktok.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tiktok.demo.entity.VideoSignedUrl;

@Repository
public interface  VideoSignedUrlRepository extends JpaRepository<VideoSignedUrl, String> {
    Optional<VideoSignedUrl> findByVideoId(String videoId);
}
