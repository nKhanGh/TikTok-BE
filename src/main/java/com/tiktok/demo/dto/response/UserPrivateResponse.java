package com.tiktok.demo.dto.response;

import java.time.LocalDate;
import java.util.Set;

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
public class UserPrivateResponse {
    String id;
    String username;
    String name;
    String email;
    String avatarUrl;
    String bio;
    LocalDate dob;
    boolean isDeleted;
    Set<RoleResponse> roles;
}
