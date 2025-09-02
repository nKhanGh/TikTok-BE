package com.tiktok.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tiktok.demo.entity.VideoSignedUrl;
import java.util.Date;


@Repository
public interface  VideoSignedUrlRepository extends JpaRepository<VideoSignedUrl, String> {
    Optional<VideoSignedUrl> findByVideoId(String videoId);
    List<VideoSignedUrl> findByExpireAtBefore(Date expireAt);
}
