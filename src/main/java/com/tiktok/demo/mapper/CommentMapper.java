package com.tiktok.demo.mapper;

import org.mapstruct.Mapper;

import com.tiktok.demo.dto.request.CommentRequest;
import com.tiktok.demo.dto.response.CommentResponse;
import com.tiktok.demo.entity.Comment;

@Mapper(componentModel="spring")
public interface CommentMapper {
    Comment toComment(CommentRequest request);
    
    CommentResponse toCommentResponse(Comment comment);
}
