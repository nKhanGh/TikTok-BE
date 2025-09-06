package com.tiktok.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.tiktok.demo.dto.ApiResponse;
import com.tiktok.demo.dto.request.CommentRequest;
import com.tiktok.demo.dto.response.CommentResponse;
import com.tiktok.demo.service.CommentService;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.tiktok.demo.dto.response.CommentPageResponse;



@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {
    CommentService commentService;

    @PostMapping("/{videoId}")
    ApiResponse<CommentResponse> createComment(@PathVariable String videoId, @RequestBody CommentRequest request){
        return ApiResponse.<CommentResponse>builder()
            .result(commentService.createComment(videoId, request))
            .build();
    }

    @GetMapping("/byVideo/{videoId}")
    ApiResponse<CommentPageResponse> getCommentsByVideo(
        @PathVariable String videoId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20")int size
    ){
        return ApiResponse.<CommentPageResponse>builder()
            .result(commentService.getCommentsByVideo(videoId, page, size))
            .build();
    }

    @GetMapping("/replies/{commentId}")
    ApiResponse<CommentPageResponse> getRepliesOfComment(
        @PathVariable String commentId,
        @RequestParam(defaultValue="0") int page,
        @RequestParam(defaultValue="3") int size
    ){
        return ApiResponse.<CommentPageResponse>builder()
            .result(commentService.getRepliesOfComment(commentId, page, size))
            .build();
    }

    @DeleteMapping("/{commentId}")
    ApiResponse<?> deleteComment(@PathVariable String commentId){
        commentService.deleteComment(commentId);
        return ApiResponse.builder()
            .message("Comment has been deleted!")
            .build();
    }

    @GetMapping("/{commentId}")
    ApiResponse<CommentResponse> getComment(@PathVariable String commentId){
        return ApiResponse.<CommentResponse>builder()
            .result(commentService.getComment(commentId))
            .build();
    }
    

    

    @PostMapping("/{commentId}/like")
    ApiResponse likeComment(@PathVariable String commentId){
        return ApiResponse.builder()
            .message(commentService.likeComment(commentId))
            .build();
    }
    
    
    
}
