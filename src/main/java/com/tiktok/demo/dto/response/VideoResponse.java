package com.tiktok.demo.dto.response;

import java.util.Date;
import java.util.Set;

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
public class VideoResponse {
    String id;
    String caption;
    int viewCount;
    int likeCount;
    int commentCount;
    Date createAt;
    UserPublicResponse userPost;
    Set<UserPublicResponse> userLiked;
    Set<HashtagResponse> hashtags;
}
