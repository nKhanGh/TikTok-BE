package com.tiktok.demo.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.backblaze.b2.client.B2StorageClient;
import com.backblaze.b2.client.B2StorageClientFactory;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Configuration
@FieldDefaults(level=AccessLevel.PRIVATE)
public class B2Config {
    @Value("${b2.applicationKeyId}")
    String applicationKeyId;

    @Value("${b2.applicationKey}")
    String applicationKey;

    @Bean
    public B2StorageClient b2StorageClient(){
        return B2StorageClientFactory.createDefaultFactory().create(applicationKeyId, applicationKey, "myTiktok");
    }

}
