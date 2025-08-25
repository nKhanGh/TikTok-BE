package com.tiktok.demo.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.backblaze.b2.client.B2StorageClient;
import com.backblaze.b2.client.B2StorageClientFactory;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Configuration
@FieldDefaults(level=AccessLevel.PRIVATE)
public class B2Config {
    @Bean
    public B2StorageClient b2StorageClient(){
        return B2StorageClientFactory.createDefaultFactory().create(applicationKeyId, applicationKey, "myTiktok");
    }
}
