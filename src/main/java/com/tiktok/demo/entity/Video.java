package com.tiktok.demo.entity;

import java.util.Date;
import java.util.Set;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Video {
    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    String id;
    String videoFileName;
    String caption;
    int viewCount;
    int likeCount;
    int commentCount;
    Date createAt;


    @ManyToOne
    @JoinColumn(name = "user_id")
    User userPost;

    @ManyToMany
    @JoinTable(
        name = "user_like_video",
        joinColumns = @JoinColumn(name = "video_id"),
        inverseJoinColumns= @JoinColumn(name = "user_id")
    )
    Set<User> userLiked;

    @ManyToOne
    @JoinColumn(name = "music_id")
    Music music;

    @ManyToMany
    @JoinTable(
        name = "hashtag_of_video",
        joinColumns = @JoinColumn(name = "video_id"),
        inverseJoinColumns = @JoinColumn(name = "hashtag_id")
    )
    Set<Hashtag> hashtags;
}
