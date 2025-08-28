package com.tiktok.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

import java.io.IOException;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/videos")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal=true)
public class VideoController {
    VideoService videoService;

    @PostMapping
    ApiResponse<VideoResponse> createVideo(@RequestBody VideoRequest request) throws B2Exception, IOException{
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
    ApiResponse deleteVideo(@PathVariable String videoId) throws B2Exception{
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
    ApiResponse<String> viewVideo(@PathVariable String videoId) throws B2Exception{
        return ApiResponse.<String>builder()
            .result(videoService.viewVideo(videoId))
            .build();
    }
    
    
    
    
}
