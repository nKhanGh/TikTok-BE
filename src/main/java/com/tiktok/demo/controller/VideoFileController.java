package com.tiktok.demo.controller;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tiktok.demo.service.VideoFileService;

import lombok.AccessLevel;

import java.io.IOException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import com.backblaze.b2.client.exceptions.B2Exception;
import com.tiktok.demo.dto.ApiResponse;
import com.tiktok.demo.dto.response.VideoFileResponse;




@SuppressWarnings("unused")
@RestController
@RequestMapping("/videoFiles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal=true)
public class VideoFileController {
    VideoFileService videoFileService;

    @PostMapping
    ApiResponse<VideoFileResponse> uploadVideo(
        @RequestParam MultipartFile videoFile
    ) throws B2Exception, IOException{
        return ApiResponse.<VideoFileResponse>builder()
            .result(videoFileService.uploadVideo(videoFile))
            .build();
    }

}
