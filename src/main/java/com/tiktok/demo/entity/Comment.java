package com.tiktok.demo.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level=AccessLevel.PRIVATE)
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    String id;
    String content;
    int likeCount;
    int repliesCount;

    @ManyToOne
    @JoinColumn(name = "video_id")
    Video video;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User userPost;

    @ManyToMany
    @JoinTable(
        name = "user_like_comment",
        joinColumns = @JoinColumn(name = "comment_id"),
        inverseJoinColumns= @JoinColumn(name = "user_id")
    )
    Set<User> userLiked;

    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    @OnDelete(action=OnDeleteAction.CASCADE)
    Comment parentComment;

    @OneToMany(mappedBy="parentComment", cascade= CascadeType.ALL)
    List<Comment> relies = new ArrayList<>();

    Date createAt;
}
