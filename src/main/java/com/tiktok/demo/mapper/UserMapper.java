package com.tiktok.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.tiktok.demo.dto.request.UserCreationRequest;
import com.tiktok.demo.dto.request.UserUpdateRequest;
import com.tiktok.demo.dto.response.UserPrivateResponse;
import com.tiktok.demo.dto.response.UserPublicResponse;
import com.tiktok.demo.entity.User;

@Mapper(componentModel="spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);

    @Mapping(target="roles", ignore=true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);

    UserPrivateResponse toUserPrivateResponse(User user);
    UserPublicResponse toUserPublicResponse(User user);

}
