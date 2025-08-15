package com.tiktok.demo.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tiktok.demo.service.AuthenticationService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal=true)
public class InvalidatedTokenScheduler {
    AuthenticationService authenticationService;

    @Scheduled(fixedRate=3600000)
    public void runTask(){
        authenticationService.removeInvalidatedToken();
    }
}
