package com.tiktok.demo.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import org.hibernate.annotations.ManyToAny;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    String id;
    String username;
    String password;
    String name;
    String email;
    String avatarUrl;
    String bio;
    boolean isDeleted;
    LocalDate dob;
    boolean isVerified = false;
    LocalDateTime createdAt = LocalDateTime.now();

    @ManyToMany
    @JoinTable(
        name = "user_has_role",
        joinColumns= @JoinColumn(name = "user_id"),
        inverseJoinColumns= @JoinColumn(name = "role_name")
    )
    Set<Role> roles;

}
