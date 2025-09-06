package com.tiktok.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tiktok.demo.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String>{
    Page<Comment> findByVideoIdAndParentCommentNull(String videoId, Pageable pageable);

    Page<Comment> findByParentCommentId(String commentId, Pageable pageable);

    int countByVideoId(String videoId);

    int countByParentCommentId(String commentId);
}
