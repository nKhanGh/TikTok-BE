package com.tiktok.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tiktok.demo.entity.Video;

@Repository
public interface VideoRepository extends JpaRepository<Video, String>{
    List<Video> findByMusicId(String musicId);
    List<Video> findByUserPostId(String userId);
    List<Video> findByHashtagsId(String hashtagId);
}
