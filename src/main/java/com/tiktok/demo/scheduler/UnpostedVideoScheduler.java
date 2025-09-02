package com.tiktok.demo.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tiktok.demo.service.VideoFileService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UnpostedVideoScheduler {
    VideoFileService videoFileService;

    @Scheduled(fixedRate=4*3600*1000)
    void runTask(){
        videoFileService.deleteVideoScheduler();
    }
}
