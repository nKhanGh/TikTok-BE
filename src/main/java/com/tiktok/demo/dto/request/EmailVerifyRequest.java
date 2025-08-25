package com.tiktok.demo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class EmailVerifyRequest {
    @NotBlank(message="INVALID_EMAIL")
    @Email
    String email;
    @Size(min=6, max=6, message="VERIFY_CODE_INVALID")
    String verifyCode;
}
