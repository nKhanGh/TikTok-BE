package com.tiktok.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tiktok.demo.service.HashtagService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.tiktok.demo.dto.ApiResponse;
import com.tiktok.demo.dto.request.HashtagRequest;
import com.tiktok.demo.dto.response.HashtagResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/hashtags")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal=true)
public class HashtagController {
    HashtagService hashtagService;

    @PostMapping
    ApiResponse<HashtagResponse> addHashtag(@RequestBody HashtagRequest request){
        return ApiResponse.<HashtagResponse>builder()
            .result(hashtagService.createHashtag(request))
            .build();
    }

    @GetMapping
    ApiResponse<List<HashtagResponse>> getHashtags(){
        return ApiResponse.<List<HashtagResponse>>builder()
            .result(hashtagService.getHashtags())
            .build();
    }
    
    @GetMapping("{tag}")
    ApiResponse<HashtagResponse> getHashtag(@PathVariable String tag){
        return ApiResponse.<HashtagResponse>builder()
            .result(hashtagService.getHashtag(tag))
            .build();
    }
    
    
}
