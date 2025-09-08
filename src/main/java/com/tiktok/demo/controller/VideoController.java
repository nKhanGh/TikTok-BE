package com.tiktok.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tiktok.demo.service.VideoService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.backblaze.b2.client.exceptions.B2Exception;
import com.tiktok.demo.dto.ApiResponse;
import com.tiktok.demo.dto.request.VideoRequest;
import com.tiktok.demo.dto.response.VideoResponse;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.tiktok.demo.dto.response.VideoPageResponse;




@SuppressWarnings("unused")
@RestController
@RequestMapping("/videos")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal=true)
public class VideoController {
    VideoService videoService;

    @PostMapping
    ApiResponse<VideoResponse> createVideo(@RequestBody VideoRequest request){
        return ApiResponse.<VideoResponse>builder()
            .result(videoService.createVideo(request))
            .build();
    }

    @GetMapping
    ApiResponse<List<VideoResponse>> getVideos(){
        return ApiResponse.<List<VideoResponse>>builder()
            .result(videoService.getVideos())
            .build();
    }

    @GetMapping("/public/{videoId}")
    ApiResponse<VideoResponse> getVideo(@PathVariable String videoId){
        return ApiResponse.<VideoResponse>builder()
            .result(videoService.getVideo(videoId))
            .build();
    }

    @DeleteMapping("/{videoId}")
    ApiResponse<Void> deleteVideo(@PathVariable String videoId) throws B2Exception{
        videoService.deleteVideo(videoId);
        return ApiResponse.<Void>builder()
            .message("Video has been deleted!")
            .build();
    }
    
    @PostMapping("/{videoId}/like")
    ApiResponse<Void> likeVideo(@PathVariable String videoId){
        return ApiResponse.<Void>builder()
            .message(videoService.likeVideo(videoId))
            .build();
    }

    @PostMapping("/public/{videoId}/view")
    ApiResponse<String> viewVideo(@PathVariable String videoId){
        return ApiResponse.<String>builder()
            .result(videoService.viewVideo(videoId, true))
            .build();
    }

    @GetMapping("/public/{videoId}/get")
    ResponseEntity<Void> getVideoUrl(@PathVariable String videoId){
        String signedUrl = videoService.viewVideo(videoId, false);
        return ResponseEntity.status(302)
            .header(HttpHeaders.LOCATION, signedUrl)
            .build();
    }

    @GetMapping("/public/byUser/{username}")
    ApiResponse<List<String>> getVideoByUser(@PathVariable String username){
        return ApiResponse.<List<String>>builder()
            .result(videoService.getVideoByUser(username))
            .build();
    }

    //limit: sum element of 1 page
    //cursor: order of next element
    @GetMapping("/public/byUser/{username}/paged")
    ApiResponse<VideoPageResponse> getVideoByUser(
        @PathVariable String username,
        @RequestParam(defaultValue="0") int cursor,
        @RequestParam(defaultValue="20") int limit
    ){
        return ApiResponse.<VideoPageResponse>builder()
            .result(videoService.getVideoByUser(username, cursor, limit))
            .build();
    }
    
    
    
    
    
    
    
    
}
