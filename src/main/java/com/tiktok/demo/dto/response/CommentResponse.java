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
public class CommentResponse {
    String id;
    String content;
    VideoResponse video;
    UserPublicResponse userPost;
    CommentResponse parentComment;
    Date createAt;
    int likeCount;
    int repliesCount;   
    Set<UserPublicResponse> userLiked;
}
