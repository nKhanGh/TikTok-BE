package com.tiktok.demo.service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.tiktok.demo.dto.request.CommentRequest;
import com.tiktok.demo.dto.response.CommentResponse;
import com.tiktok.demo.entity.Comment;
import com.tiktok.demo.entity.User;
import com.tiktok.demo.entity.Video;
import com.tiktok.demo.exception.AppException;
import com.tiktok.demo.exception.ErrorCode;
import com.tiktok.demo.mapper.CommentMapper;
import com.tiktok.demo.repository.CommentRepository;
import com.tiktok.demo.repository.UserRepository;
import com.tiktok.demo.repository.VideoRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal=true)
public class CommentService {
    CommentRepository commentRepository;
    VideoRepository videoRepository;
    UserRepository userRepository;

    CommentMapper commentMapper;

    public CommentResponse createComment(CommentRequest request){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Video video = videoRepository.findById(request.getVideoId())
            .orElseThrow(() -> new AppException(ErrorCode.VIDEO_NOT_EXISTED));

        Comment parentComment = null;
        String parentId = request.getParentCommentId();
        if(parentId != null && !parentId.equals("")){
            parentComment = commentRepository.findById(parentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_EXISTED));
            if(!parentComment.getVideo().getId().equals(request.getVideoId()))
                throw new AppException(ErrorCode.COMMENT_CONFLICT);
        }

        Comment comment = commentMapper.toComment(request);
        comment.setUserPost(user);
        comment.setVideo(video);
        comment.setLikeCount(0);
        comment.setParentComment(parentComment);
        comment.setUserLiked(new HashSet<>());
        comment.setCreateAt(new Date());
        return commentMapper.toCommentResponse(commentRepository.save(comment));
    }

    public List<CommentResponse> getCommentsByVideo(String videoId){
        return commentRepository.findByVideo_id(videoId)
            .stream().map(commentMapper::toCommentResponse).toList();
    }

    public void deleteComment(String id){
        Comment comment = commentRepository.findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_EXISTED));
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var usernameRequest = authentication.getName();
        boolean isUserPost = usernameRequest.equals(comment.getUserPost().getUsername());

        boolean isAdmin = authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ADMIN_DELETE_VIDEO"));

        String usernamePostVideo = comment.getVideo().getUserPost().getUsername();
        boolean isUserPostVideo = usernameRequest.equals(usernamePostVideo);

        if(!(isUserPost || isAdmin || isUserPostVideo))
            throw new AppException(ErrorCode.UNAUTHORIZED);
        
        commentRepository.deleteById(id);
    }

    public CommentResponse getComment(String id){
        Comment comment = commentRepository.findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_EXISTED));
        return commentMapper.toCommentResponse(comment);
    }

    public String likeComment(String id){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Comment comment = commentRepository.findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_EXISTED));
        String message;
        if(comment.getUserLiked().contains(user)){
            comment.getUserLiked().remove(user);
            comment.setLikeCount(comment.getLikeCount() - 1);
            message = "You have unliked this comment!";
        } else {
            comment.getUserLiked().add(user);
            comment.setLikeCount(comment.getLikeCount() + 1);
            message = "You have just like this comment!";
        }
        commentRepository.save(comment);
        return message;
    }
}
