package com.tiktok.demo.service;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.backblaze.b2.client.B2StorageClient;
import com.backblaze.b2.client.exceptions.B2Exception;
import com.tiktok.demo.dto.request.VideoRequest;
import com.tiktok.demo.dto.response.VideoResponse;
import com.tiktok.demo.entity.Hashtag;
import com.tiktok.demo.entity.Music;
import com.tiktok.demo.entity.User;
import com.tiktok.demo.entity.Video;
import com.tiktok.demo.entity.VideoFile;
import com.tiktok.demo.entity.VideoSignedUrl;
import com.tiktok.demo.exception.AppException;
import com.tiktok.demo.exception.ErrorCode;
import com.tiktok.demo.mapper.VideoMapper;
import com.tiktok.demo.repository.MusicRepository;
import com.tiktok.demo.repository.UserRepository;
import com.tiktok.demo.repository.VideoRepository;
import com.tiktok.demo.repository.VideoSignedUrlRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class VideoService {
    VideoRepository videoRepository;
    VideoSignedUrlRepository videoSignedUrlRepository;
    MusicRepository musicRepository;
    UserRepository userRepository;

    VideoMapper videoMapper;

    HashtagService hashtagService;
    VideoFileService videoFileService;

    public VideoResponse createVideo(VideoRequest request)
            throws B2Exception, IOException {
        Music music = request.getMusicId() != null
                ? musicRepository.findById(request.getMusicId())
                        .orElseThrow(() -> new AppException(ErrorCode.MUSIC_NOT_EXISTED))
                : null;

        String usernamePost = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(usernamePost)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Set<Hashtag> setHashtags = new HashSet<>();
        if(request.getHashtags() != null)
            request.getHashtags().forEach(tag -> {
                Hashtag hashtag = hashtagService.handleCreateHashtag(tag);
                setHashtags.add(hashtag);
            });

        VideoFile videoFile = videoFileService.getVideo(request.getVideoFileId());

        Video video = Video.builder()
                .music(music)
                .caption(request.getCaption())
                .videoFile(videoFile)
                .viewCount(0)
                .likeCount(0)
                .commentCount(0)
                .createAt(new Date())
                .userLiked(new HashSet<>())
                .hashtags(setHashtags)
                .userPost(user)
                .build();
        
        return videoMapper.toVideoResponse(videoRepository.save(video));
    }

    public String viewVideo(String id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.VIDEO_NOT_EXISTED));
        String fileName = video.getVideoFile().getVideoFileName();
        video.setViewCount(video.getViewCount() + 1);
        var videoSignedUrl = videoSignedUrlRepository.findByVideoId(id);
        if (videoSignedUrl.isPresent() && videoSignedUrl.get().getExpireAt().after(new Date())) {
            return videoSignedUrl.get().getSignedUrl();
        }

        String tempUrl = videoFileService.getVideoUrl(fileName);
        VideoSignedUrl newVideoSignedUrl = VideoSignedUrl.builder()
                .createdAt(new Date())
                .expireAt(new Date(Instant.now().plus(3600, ChronoUnit.SECONDS).toEpochMilli()))
                .videoId(id)
                .signedUrl(tempUrl)
                .build();
        videoSignedUrlRepository.save(newVideoSignedUrl);
        videoRepository.save(video);
        return tempUrl;
    }

    public List<VideoResponse> getVideos() {
        return videoRepository.findAll().stream().map(videoMapper::toVideoResponse).toList();
    }

    public VideoResponse getVideo(String id) {
        Video video = videoRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.VIDEO_NOT_EXISTED));
        return videoMapper.toVideoResponse(video);
    }

    public void deleteVideo(String id) throws B2Exception {
        Video video = videoRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.VIDEO_NOT_EXISTED));
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN_DELETE_VIDEO"));

        if (!isAdmin && !username.equals(video.getUserPost().getUsername()))
            throw new AppException(ErrorCode.UNAUTHORIZED);
        
        VideoFile videoFile = video.getVideoFile();

        videoFileService.deleteVideo(videoFile.getVideoFileName(), videoFile.getVideoFileId());
        videoRepository.deleteById(id);
    }

    public String likeVideo(String id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.VIDEO_NOT_EXISTED));
        String message;
        if (video.getUserLiked().contains(user)) {
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

    // public Page<VideoResponse> getVideosByFilter(String hashtag, String musicId,
    // String sort, int page, int size){

    // }
}
