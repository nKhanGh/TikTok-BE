package com.tiktok.demo.entity;

import java.util.Set;

import jakarta.persistence.Entity;
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
public class Role {
    @Id
    String name;
    String description;

    @ManyToMany
    @JoinTable(
        name = "permission_of_role",
        joinColumns= @JoinColumn(name = "role_name"),
        inverseJoinColumns= @JoinColumn(name = "permission_name")

    )
    Set<Permission> permissions;
}
