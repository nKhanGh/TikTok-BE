package com.tiktok.demo.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.tiktok.demo.dto.ApiResponse;
import com.tiktok.demo.dto.request.EmailRequest;
import com.tiktok.demo.dto.request.UserRegisterRequest;
import com.tiktok.demo.dto.request.EmailVerifyRequest;
import com.tiktok.demo.dto.response.EmailVerifyResponse;
import com.tiktok.demo.service.EmailService;

import jakarta.validation.Valid;



@SuppressWarnings("unused")
@RestController
@RequestMapping("/emails")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal=true)
public class EmailController {

    EmailService emailService;

    @SuppressWarnings("rawtypes")
    @PostMapping("/sender")
    ApiResponse sendEmail(@RequestBody @Valid EmailRequest request){
        emailService.sendEmail(request);
        return ApiResponse.builder()
            .message("Email has been sent!")
            .build();       
    }

    @PostMapping("/sender/verifyCode")
    ApiResponse<Void> sendVerificationCode(@RequestBody @Valid UserRegisterRequest request){
        emailService.sendVerificationCode(request);
        return ApiResponse.<Void>builder()
            .message("Verification code has been sent!")
            .build();
    }

    @PostMapping("/verify")
    ApiResponse<EmailVerifyResponse> verifyEmail(@RequestBody @Valid EmailVerifyRequest request){
        return ApiResponse.<EmailVerifyResponse>builder()
            .result(emailService.verifyEmail(request))
            .build();
    }
    
    
    
}
