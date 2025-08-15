package com.tiktok.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

import com.tiktok.demo.service.AuthenticationService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.text.ParseException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.nimbusds.jose.JOSEException;
import com.tiktok.demo.dto.ApiResponse;
import com.tiktok.demo.dto.request.AuthenticationRequest;
import com.tiktok.demo.dto.request.IntrospectRequest;
import com.tiktok.demo.dto.request.LogoutRequest;
import com.tiktok.demo.dto.request.RefreshTokenRequest;
import com.tiktok.demo.dto.response.AuthenticationResponse;
import com.tiktok.demo.dto.response.IntrospectResponse;
import com.tiktok.demo.dto.response.LogoutResponse;
import com.tiktok.demo.dto.response.RefreshTokenResponse;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal=true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/login")
    ApiResponse<AuthenticationResponse> login(@RequestBody AuthenticationRequest request){
        return ApiResponse.<AuthenticationResponse>builder()
            .result(authenticationService.login(request))
            .build();
    }

    @PostMapping("/logout")
    ApiResponse<LogoutResponse> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException{
        return ApiResponse.<LogoutResponse>builder()
            .result(authenticationService.logout(request))
            .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request){
        return ApiResponse.<IntrospectResponse>builder()
            .result(authenticationService.introspect(request))
            .build();
    }

    @PostMapping("/refreshToken")
    ApiResponse<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) throws ParseException, JOSEException{
        return ApiResponse.<RefreshTokenResponse>builder()
            .result(authenticationService.refreshToken(request))
            .build();
    }
    
    
    
}
