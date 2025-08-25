package com.tiktok.demo.dto.request;

import com.tiktok.demo.validator.UsernameConstraint;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
@FieldDefaults(level =AccessLevel.PRIVATE)
public class UsernameAddRequest {
    @Email
    @NotBlank(message="EMAIL_INVALID")
    String email;

    @UsernameConstraint
    String username;
}
