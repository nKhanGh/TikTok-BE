package com.tiktok.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tiktok.demo.entity.UserRelation;
import com.tiktok.demo.entity.id.UserRelationId;

import java.util.List;

import com.tiktok.demo.entity.User;


@Repository
public interface UserRelationRepository extends JpaRepository<UserRelation, UserRelationId>{
    Optional<UserRelation> findByUserFollowAndUserFollowed(User userFollow, User userFollowed);
}
