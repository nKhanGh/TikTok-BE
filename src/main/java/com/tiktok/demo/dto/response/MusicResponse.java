package com.tiktok.demo.dto.response;

import java.util.Date;

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
public class MusicResponse {
    String id;
    String title;
    String author;
    String audioUrl;
    UserPublicResponse userPost;
    Date createAt;
}
