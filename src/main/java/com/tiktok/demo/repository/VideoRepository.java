package com.tiktok.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tiktok.demo.entity.Video;

@Repository
public interface VideoRepository extends JpaRepository<Video, String>{
    List<Video> findByMusic_id(String musicId);
    List<Video> findByUserPost_id(String userId);
    List<Video> findByHashtags_id(String hashtagId);
}
