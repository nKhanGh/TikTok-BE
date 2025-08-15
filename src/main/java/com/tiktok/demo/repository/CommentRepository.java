package com.tiktok.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tiktok.demo.entity.Comment;
import com.tiktok.demo.entity.Video;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String>{
    List<Comment> findByVideo_id(String videoId);
}
