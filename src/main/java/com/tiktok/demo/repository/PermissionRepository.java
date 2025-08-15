package com.tiktok.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tiktok.demo.entity.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String>{
    
}
