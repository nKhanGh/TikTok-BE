package com.tiktok.demo.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.backblaze.b2.client.B2StorageClient;
import com.backblaze.b2.client.B2StorageClientFactory;

@Configuration
public class B2Config {
    @Value("${b2.keyId}")
    String keyId;

    @Value("${b2.applicationKey}")
    String applicationKey;

    @Bean
    B2StorageClient b2StorageClient() {
        return B2StorageClientFactory.createDefaultFactory().create(keyId, applicationKey, "myTiktok");
    }
}
