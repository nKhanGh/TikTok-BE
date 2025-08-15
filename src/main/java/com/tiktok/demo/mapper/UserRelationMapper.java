package com.tiktok.demo.mapper;

import org.mapstruct.Mapper;

import com.tiktok.demo.dto.response.UserRelationResponse;
import com.tiktok.demo.entity.UserRelation;

@Mapper(componentModel = "spring")
public interface UserRelationMapper {
    UserRelationResponse toUserRelationResponse(UserRelation userRelation);
}
