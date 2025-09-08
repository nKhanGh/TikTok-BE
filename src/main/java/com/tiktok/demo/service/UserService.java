package com.tiktok.demo.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.backblaze.b2.client.exceptions.B2Exception;
import com.tiktok.demo.dto.request.UserCreationRequest;
import com.tiktok.demo.dto.request.UserUpdateRequest;
import com.tiktok.demo.dto.request.UsernameAddRequest;
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
import main.java.com.tiktok.demo.dto.response.UserRelationPageResponse;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal=true)
@Slf4j
public class UserService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    UserRelationRepository userRelationRepository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

    UserMapper userMapper;    

    ImageService imageService;
    VideoService videoService;

    public UserPrivateResponse createUser(UserCreationRequest request, boolean isVerified){
        if(request.getUsername() != null && userRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);
        User user = userMapper.toUser(request);
        
        var roles = roleRepository.findAllById(List.of("USER"));
        user.setRoles(new HashSet<>(roles));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setDeleted(false);
        user.setAvatarUrl("https://f003.backblazeb2.com/file/tiktokImages14102005/default_avatar.jpg");
        user.setVerified(isVerified);
        user.setCreatedAt(LocalDateTime.now());

        return userMapper.toUserPrivateResponse(userRepository.save(user));
    }

    public UserPublicResponse addUsername(UsernameAddRequest request){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        if(userRepository.existsByUsername(request.getUsername())){
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        user.setUsername(request.getUsername());
        user.setAvatarUrl("https://f003.backblazeb2.com/file/tiktokImages14102005/default_avatar.jpg");
        return userMapper.toUserPublicResponse(userRepository.save(user));
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


    public UserPublicResponse getMyInfo(){
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserPublicResponse(user);
    }

    public UserPublicResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        UserPublicResponse userResponse = userMapper.toUserPublicResponse(user);
        int totalLike = videoService.getTotalLikeCount(user.getId());
        List<UserRelation> followers = userRelationRepository.findByUserFollowedId(user.getId());
        int followerCount = (int) followers.stream()
                .filter(follower -> follower.getStatus().equals(FollowStatus.FOLLOW))
                .count();
        List<UserRelation> followings = userRelationRepository.findByUserFollowId(user.getId());
        int followingCount = (int) followings.stream()
                .filter(following -> following.getStatus().equals(FollowStatus.FOLLOW))
                .count();
        userResponse.setFollowerCount(followerCount);
        userResponse.setFollowingCount(followingCount);
        userResponse.setLikeCount(totalLike);
        return userResponse;
    }


    public String addFollowStatus(String userId, String followStatus){
        String idUserFollow = SecurityContextHolder.getContext().getAuthentication().getName();
        User userFollow = userRepository.findById(idUserFollow)
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
        String endMessage = "ed this user!";
        if(oldRelation.isPresent()){
            if(status.equals(oldRelation.get().getStatus())){
                userRelationRepository.deleteById(userRelationId);
                message = "You have un" + followStatus.toLowerCase() + endMessage;
            } else {
                oldRelation.get().setStatus(status);
                userRelationRepository.save(oldRelation.get());
                message = "You have " + followStatus.toLowerCase() + endMessage;
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

    public boolean isFollow(String userId){
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        var relation = userRelationRepository.findByUserFollowIdAndUserFollowedId(id, userId);
        return relation.isPresent() && relation.get().getStatus().equals(FollowStatus.FOLLOW);
    }

    public void deleteNotVerifiedUser(){
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(1);
        List<User> notVerifiedUsers = userRepository.findAllByIsVerifiedFalseAndCreatedAtBefore(threshold);
        notVerifiedUsers.forEach(user -> userRepository.deleteById(user.getId()));
    }

    public boolean usernameExist(String username){
        return userRepository.existsByUsername(username);
    }

    public UserPublicResponse setAvatar(MultipartFile avatarFile) throws B2Exception, IOException{
        String[] avatar = imageService.uploadImage(avatarFile);
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        if(user.getAvatarFileId() != null){
            imageService.deleteImage(user.getAvatarUrl(), user.getAvatarFileId());
        }
        user.setAvatarUrl(avatar[0]);
        user.setAvatarFileId(avatar[1]);
        userRepository.save(user);
        return userMapper.toUserPublicResponse(user);
    }


    public String getAvatar(){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        if(user.getAvatarUrl() == null) return "api/images/default_avatar.jpg";
        return imageService.getImage(user.getAvatarUrl());
    }

    public UserPublicResponse updatePublicUser(MultipartFile avatarFile, String username, String name, String bio) throws B2Exception, IOException{
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        if(!username.equals(user.getUsername()) && userRepository.existsByUsername(username))
            throw new AppException(ErrorCode.USER_EXISTED);
    
        if(user.getAvatarFileId() != null)
            imageService.deleteImage(user.getAvatarUrl(), user.getAvatarFileId());
        if(avatarFile != null){
            String[] avatar = imageService.uploadImage(avatarFile);
            user.setAvatarFileId(avatar[1]);
            user.setAvatarUrl(avatar[0]);
        }
        user.setUsername(username);
        user.setName(name);
        user.setBio(bio);
        return userMapper.toUserPublicResponse(userRepository.save(user));
    }

    public boolean existByUsername(String username){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        if(username.equals(user.getUsername())) return false;
        return userRepository.existsByUsername(username);
    }

    public UserRelationPageResponse getFollowedUser(int page, int size){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!userRepository.existsById(userId))
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        Pageable pageable = PageRequest.of(page, size);
        List<UserRelation> relations = userRelationRepository.findByUserFollowId(userId, pageable);
        List<User> followedUsers = relations.stream()
            .map(relation -> userRepository.findById(relation.getUserFollowed().getId())
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)))
            .toList();

        List<UserPublicResponse> response = followedUsers.stream().map(userMapper::toUserPublicResponse).toList();
        int total = userRelationRepository.countByUserFollowId(userId);
        boolean hasMore = (page + 1) * size < total;
        int nextPage = hasMore ? page + 1 : page;

        return UserRelationPageResponse.builder()
            .followedUsers(response)
            .hasMore(hasMore)
            .nextPage(nextPage)
            .build();
    }
}
