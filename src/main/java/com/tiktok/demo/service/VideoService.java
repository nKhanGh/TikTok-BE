package com.tiktok.demo.service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.query.Page;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.tiktok.demo.dto.request.VideoRequest;
import com.tiktok.demo.dto.response.VideoResponse;
import com.tiktok.demo.entity.Hashtag;
import com.tiktok.demo.entity.Music;
import com.tiktok.demo.entity.User;
import com.tiktok.demo.entity.Video;
import com.tiktok.demo.exception.AppException;
import com.tiktok.demo.exception.ErrorCode;
import com.tiktok.demo.mapper.VideoMapper;
import com.tiktok.demo.repository.MusicRepository;
import com.tiktok.demo.repository.UserRepository;
import com.tiktok.demo.repository.VideoRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal=true)
@Slf4j
public class VideoService {
    VideoRepository videoRepository;
    MusicRepository musicRepository;
    UserRepository userRepository;
    
    VideoMapper videoMapper;

    HashtagService hashtagService;

    public VideoResponse createVideo(VideoRequest request){
        Video video = videoMapper.toVideo(request);
        Music music = musicRepository.findById(request.getMusicId())
            .orElseThrow(() -> new AppException(ErrorCode.MUSIC_NOT_EXISTED));
        video.setMusic(music);
        video.setCreateAt(new Date());  
        video.setViewCount(0);
        video.setLikeCount(0);  
        video.setCommentCount(0); 
        video.setUserLiked(new HashSet<>());

        String usernamePost = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(usernamePost)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        video.setUserPost(user);
        log.info(user.toString());
        log.info(usernamePost);

        Set<Hashtag> hashtags = new HashSet<>();
        request.getHashtags().forEach(tag -> {
            Hashtag hashtag = hashtagService.handleCreateHashtag(tag);
            hashtags.add(hashtag);
        });
        video.setHashtags(hashtags);
        log.info(video.getUserPost().toString());
        return videoMapper.toVideoResponse(videoRepository.save(video));
    }

    public List<VideoResponse> getVideos(){
        return videoRepository.findAll().stream().map(videoMapper::toVideoResponse).toList();
    }

    public VideoResponse getVideo(String id){
        Video video = videoRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.VIDEO_NOT_EXISTED));
        return videoMapper.toVideoResponse(video);
    }

    public void deleteVideo(String id){
        Video video = videoRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.VIDEO_NOT_EXISTED));
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ADMIN_DELETE_VIDEO"));

        if(!isAdmin && !username.equals(video.getUserPost().getUsername()))
            throw new AppException(ErrorCode.UNAUTHORIZED);

        videoRepository.deleteById(id);
    }

    public String likeVideo(String id){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Video video = videoRepository.findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.VIDEO_NOT_EXISTED));
        String message;
        if(video.getUserLiked().contains(user)){
            video.getUserLiked().remove(user);
            video.setLikeCount(video.getLikeCount() - 1);
            message = "You have unliked this video!";
        } else {
            video.getUserLiked().add(user);
            video.setLikeCount(video.getLikeCount() + 1);
            message = "You have just liked this video!";
        }

        videoRepository.save(video);
        return message;
    }

    public void viewVideo(String id){
        Video video = videoRepository.findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.VIDEO_NOT_EXISTED));
        video.setViewCount(video.getViewCount() + 1);
        videoRepository.save(video);
    }






    // public Page<VideoResponse> getVideosByFilter(String hashtag, String musicId, String sort, int page, int size){

    // }
}
