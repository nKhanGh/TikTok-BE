package com.tiktok.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tiktok.demo.service.MusicService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.tiktok.demo.dto.ApiResponse;
import com.tiktok.demo.dto.request.MusicRequest;
import com.tiktok.demo.dto.response.MusicResponse;

import org.springframework.web.bind.annotation.GetMapping;


@SuppressWarnings("unused")
@RestController
@RequestMapping("/musics")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal=true)
public class MusicController {
    MusicService musicService;

    @PostMapping
    ApiResponse<MusicResponse> addMusic(@RequestBody MusicRequest request){
        return ApiResponse.<MusicResponse>builder()
            .result(musicService.addMusic(request))
            .build();
    }

    @DeleteMapping("/{musicId}")
    ApiResponse<String> deleteMusic(@PathVariable String musicId){
        musicService.deleteMusic(musicId);
        return ApiResponse.<String>builder()
            .result("Music has been deleted!")
            .build();
    }

    @GetMapping("/{musicId}")
    ApiResponse<MusicResponse> getMusic(@PathVariable String musicId){
        return ApiResponse.<MusicResponse>builder()
            .result(musicService.getMusic(musicId))
            .build();
    }

    @GetMapping
    ApiResponse<List<MusicResponse>> getMusics(){
        return ApiResponse.<List<MusicResponse>>builder()
            .result(musicService.getMusics())
            .build();
    }
    
    
    
}
