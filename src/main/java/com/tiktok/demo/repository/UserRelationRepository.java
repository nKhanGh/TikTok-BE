package com.tiktok.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tiktok.demo.entity.UserRelation;
import com.tiktok.demo.entity.id.UserRelationId;

@Repository
public interface UserRelationRepository extends JpaRepository<UserRelation, UserRelationId>{
    Optional<UserRelation> findByUserFollowIdAndUserFollowedId(String userFollowId, String userFollowedId);
    List<UserRelation> findByUserFollowId(String userId, Pageable pageable);
    int countByUserFollowId(String userId);
}
