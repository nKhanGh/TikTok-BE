package com.tiktok.demo.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
public class VideoSignedUrl {
    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    String id;
    String videoId;
    
    @Column(name = "signed_url", length = 2000)
    String signedUrl;
    Date expireAt;
    Date createdAt;
}
