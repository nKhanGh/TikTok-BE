package com.tiktok.demo.configuration;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class AwsConfig {

    @Value("${b2.keyId}")
    String keyId;

    @Value("${b2.applicationKey}")
    String applicationKey;

    @Bean
    S3Presigner s3Presigner(){
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(keyId, applicationKey);
        return S3Presigner.builder()
            .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
            .region(Region.EU_CENTRAL_1)
            .endpointOverride(URI.create("https://s3.eu-central-003.backblazeb2.com"))
            .build();
    }
}
