package com.tiktok.demo.service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tiktok.demo.dto.request.UserCreationRequest;
import com.tiktok.demo.dto.request.UserUpdateRequest;
import com.tiktok.demo.dto.response.UserPrivateResponse;
import com.tiktok.demo.dto.response.UserPublicResponse;
import com.tiktok.demo.entity.User;
import com.tiktok.demo.entity.UserRelation;
import com.tiktok.demo.entity.id.UserRelationId;
import com.tiktok.demo.enums.FollowStatus;
import com.tiktok.demo.exception.AppException;
import com.tiktok.demo.exception.ErrorCode;
import com.tiktok.demo.mapper.UserMapper;
import com.tiktok.demo.repository.RoleRepository;
import com.tiktok.demo.repository.UserRelationRepository;
import com.tiktok.demo.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal=true)
@Slf4j
public class UserService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    UserRelationRepository userRelationRepository;

    PasswordEncoder passwordEncoder;

    UserMapper userMapper;    

    public UserPrivateResponse createUser(UserCreationRequest request){
        if(userRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);
        if(userRepository.existsByName(request.getName()))
            throw new AppException(ErrorCode.NAME_EXISTED);
        User user = userMapper.toUser(request);
        
        var roles = roleRepository.findAllById(List.of("USER"));
        user.setRoles(new HashSet<>(roles));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setDeleted(false);

        return userMapper.toUserPrivateResponse(userRepository.save(user));
    }

    @PostAuthorize("hasAuthority('ADMIN_EDIT_USER') or returnObject.username == authentication.name")
    public UserPrivateResponse updateUser(String userId, UserUpdateRequest request){
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        
        userMapper.updateUser(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        var roles = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));

        return userMapper.toUserPrivateResponse(userRepository.save(user));
    }
    @PreAuthorize("hasAuthority('ADMIN_VIEW_USER')")
    public List<UserPrivateResponse> getUsersByAdmin(){
        log.info(SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString());
        return userRepository.findAll().stream().map(userMapper::toUserPrivateResponse).toList();
    }

    public List<UserPublicResponse> getUsersByUser(){
        return userRepository.findAll().stream().map(userMapper::toUserPublicResponse).toList();
    }

    public UserPublicResponse getUserByUser(String id){
        return userMapper.toUserPublicResponse(userRepository.findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }

    @PostAuthorize("hasAuthority('ADMIN_VIEW_USER') or returnObject.username == authentication.name")
    public UserPrivateResponse getUserByAdmin(String id){
        return userMapper.toUserPrivateResponse(userRepository.findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }


    @PostAuthorize("hasAuthority('ADMIN_DELETE_USER') or returnObject.username == authentication.name")
    public void deleteUser(String id){
        
        userRepository.deleteById(id);
    }


    public UserPrivateResponse getMyInfo(){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserPrivateResponse(user);
    }


    public String addFollowStatus(String userId, String followStatus){
        String usernameFollow = SecurityContextHolder.getContext().getAuthentication().getName();
        User userFollow = userRepository.findByUsername(usernameFollow)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        User userFollowed = userRepository.findById(userId)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        UserRelationId userRelationId = UserRelationId.builder()
            .userFollowId(userFollow.getId())
            .userFollowedId(userId)
            .build();
        FollowStatus status;
        try {
            status = FollowStatus.valueOf(followStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.INVALID_FOLLOW_STATUS);
        }
        var oldRelation = userRelationRepository.findById(userRelationId);
        String message;
        if(oldRelation.isPresent()){
            if(status.equals(oldRelation.get().getStatus())){
                userRelationRepository.deleteById(userRelationId);
                message = "You have un" + followStatus.toLowerCase() + "ed this user!";
            } else {
                oldRelation.get().setStatus(status);
                userRelationRepository.save(oldRelation.get());
                message = "You have " + followStatus.toLowerCase() + "ed this user!";
            }
        } else {
            UserRelation userRelation = UserRelation.builder()
                .id(userRelationId)
                .status(status)
                .createAt(new Date())
                .userFollow(userFollow)
                .userFollowed(userFollowed)
                .build();
            userRelationRepository.save(userRelation);
            message = "You have " + followStatus.toLowerCase() + "ed this user!";
        }
        return message;
    }


}
