package com.tiktok.demo.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tiktok.demo.entity.VideoFile;

@Repository
public interface VideoFileRepository extends JpaRepository<VideoFile, String>{
    List<VideoFile> findByIsPostedFalseAndUploadAtBefore(Date date);
}
