package com.tiktok.demo.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tiktok.demo.service.VideoService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Component
@RequiredArgsConstructor
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
public class SignedVideoScheduler {
    VideoService videoService;

    @Scheduled(fixedRate=14400000)
    void runTask() {
        videoService.refreshVideoSignedUrls();
    }
}
