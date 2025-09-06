package com.tiktok.demo.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tiktok.demo.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Component
@RequiredArgsConstructor
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
@SuppressWarnings("unused")
public class NotVerifiedUserScheduler {
    UserService userService;

    @Scheduled(fixedRate=86400000)
    public void runTask(){
        userService.deleteNotVerifiedUser();
    }
}
