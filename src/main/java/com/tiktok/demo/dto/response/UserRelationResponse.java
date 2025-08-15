package com.tiktok.demo.dto.response;

import java.util.Date;

import com.tiktok.demo.enums.FollowStatus;

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
public class UserRelationResponse {
    UserPublicResponse userFollowed;
    UserPublicResponse userFollow;
    FollowStatus status;
    Date createAt;
}
