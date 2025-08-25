package com.tiktok.demo.dto.request;

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
@FieldDefaults(level= AccessLevel.PRIVATE)
public class EmailRequest {
    @NotBlank(message="EMAIL_INVALID")
    @Email
    String toEmail;
    @NotBlank(message="EMAIL_SUBJECT_INVALID")
    String subject;
    @NotBlank(message="EMAIL_BODY_INVALID")
    String body;
}
