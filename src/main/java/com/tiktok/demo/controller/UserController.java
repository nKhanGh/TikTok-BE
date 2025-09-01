package com.tiktok.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tiktok.demo.service.UserService;

import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.backblaze.b2.client.exceptions.B2Exception;
import com.tiktok.demo.dto.ApiResponse;
import com.tiktok.demo.dto.request.UserCreationRequest;
import com.tiktok.demo.dto.response.UserPrivateResponse;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.multipart.MultipartFile;

import com.tiktok.demo.dto.request.UserUpdateRequest;
import com.tiktok.demo.dto.request.UsernameAddRequest;
import com.tiktok.demo.dto.request.UsernameRandomAddRequest;
import com.tiktok.demo.dto.response.UserPublicResponse;




@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal=true)
@Slf4j
public class UserController {
    UserService userService;
    
    @PostMapping
    ApiResponse<UserPrivateResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        log.info("In controller");
        return ApiResponse.<UserPrivateResponse>builder()
            .result(userService.createUser(request, true))
            .build();
    }

    @GetMapping("/private")
    ApiResponse<List<UserPrivateResponse>> getUsersByAdmin(){
        return ApiResponse.<List<UserPrivateResponse>>builder()
            .result(userService.getUsersByAdmin())
            .build();
    }

    @GetMapping("/public")
    ApiResponse<List<UserPublicResponse>> getUsersByUser(){
        return ApiResponse.<List<UserPublicResponse>>builder()
            .result(userService.getUsersByUser())
            .build();
    }
    

    @GetMapping("/private/{userId}")
    ApiResponse<UserPrivateResponse> getUserbyAdmin(@PathVariable String userId){
        return ApiResponse.<UserPrivateResponse>builder()
            .result(userService.getUserByAdmin(userId))
            .build();
    }

    @GetMapping("/public/{userId}")
    ApiResponse<UserPublicResponse> getUserbyUser(@PathVariable String userId){
        return ApiResponse.<UserPublicResponse>builder()
            .result(userService.getUserByUser(userId))
            .build();
    }
    

    @PutMapping("/{userId}")
    ApiResponse<UserPrivateResponse> updateUser(@PathVariable String userId, @RequestBody @Valid UserUpdateRequest request){
        return ApiResponse.<UserPrivateResponse>builder()
            .result(userService.updateUser(userId, request))
            .build();
    }

    @PutMapping("/updatePublic")
    ApiResponse<UserPublicResponse> updatePublicUser(
        @RequestParam(value = "avatarFile", required=false) MultipartFile avatarFile,
        @RequestParam("username") String username,
        @RequestParam("name") String name,
        @RequestParam("bio") String bio
    ) throws B2Exception, IOException{
        return ApiResponse.<UserPublicResponse>builder()
            .result(userService.updatePublicUser(avatarFile, username, name, bio))
            .build();
    }

    @PostMapping("/existed/{username}")
    ApiResponse<Boolean> existedByUsername(@PathVariable String username){
        return ApiResponse.<Boolean>builder()
            .result(userService.existByUsername(username))
            .build();
    }
    

    @DeleteMapping("/{userId}")
    ApiResponse<String> deleteUser(@PathVariable String userId){
        userService.deleteUser(userId);
        return ApiResponse.<String>builder()
            .result("User has been deleted!")
            .build();
    }

    @GetMapping("/myInfo")
    ApiResponse<UserPublicResponse> getMyInfo(){
        return ApiResponse.<UserPublicResponse>builder()
            .result(userService.getMyInfo())
            .build();
    }

    @PostMapping("/{userId}/{action}")
    ApiResponse followUser(
        @PathVariable String userId,
        @PathVariable String action
    ){
        return ApiResponse.builder()
            .message(userService.addFollowStatus(userId, action))
            .build();
    }

    @PostMapping("/set-username")
    ApiResponse<UserPublicResponse> setUsername(@RequestBody @Valid UsernameAddRequest request){
        return ApiResponse.<UserPublicResponse>builder()
            .result(userService.addUsername(request))
            .build();
    }

    @GetMapping("/exist/{username}")
    ApiResponse<Boolean> existUsername(@PathVariable String username){
        return ApiResponse.<Boolean>builder()
            .result(userService.usernameExist(username))
            .build();
    }

    @PostMapping("/avatars")
    ApiResponse<UserPublicResponse> setAvatar(
        @RequestParam("avatarFile") MultipartFile avatarFile
    ) throws B2Exception, IOException{
        return ApiResponse.<UserPublicResponse>builder()
            .result(userService.setAvatar(avatarFile))
            .build();
    }

    @GetMapping("/avatars")
    ApiResponse<String> getAvatar(){
        return ApiResponse.<String>builder()
            .result(userService.getAvatar())
            .build();
    }


    
    
    
    
    
    
}
