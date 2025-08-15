package com.tiktok.demo.entity;

import java.util.Date;

import com.tiktok.demo.entity.id.UserRelationId;
import com.tiktok.demo.enums.FollowStatus;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
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
public class UserRelation {
    @EmbeddedId
    UserRelationId id;

    @ManyToOne
    @JoinColumn(name = "user_followed_id")
    @MapsId("userFollowedId")
    User userFollowed;

    @ManyToOne
    @JoinColumn(name = "user_follow_id")
    @MapsId("userFollowId")
    User userFollow;

    FollowStatus status;

    Date createAt;

}
