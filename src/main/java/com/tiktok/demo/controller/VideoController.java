package com.tiktok.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tiktok.demo.service.VideoService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.tiktok.demo.dto.ApiResponse;
import com.tiktok.demo.dto.request.VideoRequest;
import com.tiktok.demo.dto.response.VideoResponse;
import com.tiktok.demo.repository.VideoRepository;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;



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

    @GetMapping("/{videoId}")
    ApiResponse<VideoResponse> getVideo(@PathVariable String videoId){
        return ApiResponse.<VideoResponse>builder()
            .result(videoService.getVideo(videoId))
            .build();
    }

    @DeleteMapping("/{videoId}")
    ApiResponse deleteVideo(@PathVariable String videoId){
        videoService.deleteVideo(videoId);
        return ApiResponse.builder()
            .message("Video has been deleted!")
            .build();
    }

    // @GetMapping
    // ApiResponse<List<VideoResponse> getVideoByFilter(
    //     @RequestParam(required=false) String hashtag,
    //     @RequestParam(required=false) String musicId,
    //     @RequestParam(defaultValue="views") String sort,
    //     @RequestParam(defaultValue="0") int page,
    //     @RequestParam(defaultValue="20") int size
    // ){

    // }
    
    @PostMapping("/{videoId}/like")
    ApiResponse likeVideo(@PathVariable String videoId){
        return ApiResponse.builder()
            .message(videoService.likeVideo(videoId))
            .build();
    }

    @PostMapping("/{videoId}/view")
    ApiResponse viewVideo(@PathVariable String videoId){
        videoService.viewVideo(videoId);
        return ApiResponse.builder()
            .message("You have viewed this video!")
            .build();
    }
    
    
    
    
}
