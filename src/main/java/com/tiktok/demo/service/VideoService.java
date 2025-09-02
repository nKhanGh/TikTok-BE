package com.tiktok.demo.service;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import lombok.extern.slf4j.Slf4j;

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

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findById(userId)
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
        videoRepository.save(video);
        String tempUrl = videoFileService.getVideoUrl(videoFile.getVideoFileName());
        VideoSignedUrl newVideoSignedUrl = VideoSignedUrl.builder()
                .createdAt(new Date())
                .expireAt(new Date(Instant.now().plus(16, ChronoUnit.HOURS).toEpochMilli()))
                .videoId(video.getId())
                .signedUrl(tempUrl)
                .build();
        videoSignedUrlRepository.save(newVideoSignedUrl);
        
        return videoMapper.toVideoResponse(video);
    }

    public String viewVideo(String id, boolean isIncreaseViewCount) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.VIDEO_NOT_EXISTED));
        String fileName = video.getVideoFile().getVideoFileName();
        if(isIncreaseViewCount)
            video.setViewCount(video.getViewCount() + 1);
        var videoSignedUrl = videoSignedUrlRepository.findByVideoId(id);
        if (videoSignedUrl.isPresent() && videoSignedUrl.get().getExpireAt().after(new Date())) {
            return videoSignedUrl.get().getSignedUrl();
        }

        String tempUrl = videoFileService.getVideoUrl(fileName);
        VideoSignedUrl newVideoSignedUrl = VideoSignedUrl.builder()
                .createdAt(new Date())
                .expireAt(new Date(Instant.now().plus(16, ChronoUnit.HOURS).toEpochMilli()))
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
        String userId = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN_DELETE_VIDEO"));

        if (!isAdmin && !userId.equals(video.getUserPost().getId()))
            throw new AppException(ErrorCode.UNAUTHORIZED);
        
        VideoFile videoFile = video.getVideoFile();

        videoFileService.deleteVideo(videoFile.getVideoFileName(), videoFile.getVideoFileId());
        videoRepository.deleteById(id);
    }

    public String likeVideo(String id) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findById(userId)
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

    public List<String> getVideoByUser(String userId){
        List<Video> videos = videoRepository.findByUserPostId(userId);
        return videos.stream().map(Video::getId).toList();
    }

    @Transactional
    public void refreshVideoSignedUrls(){
        Date threshold = Date.from(Instant.now().plus(5, ChronoUnit.HOURS));
        List<VideoSignedUrl> signedUrls = videoSignedUrlRepository.findByExpireAtBefore(threshold);
        signedUrls.forEach(signedUrl -> {
            Video video = videoRepository.findById(signedUrl.getVideoId())
                .orElseThrow(() -> new AppException(ErrorCode.VIDEO_NOT_EXISTED));
            String tmpUrl = videoFileService.getVideoUrl(video.getVideoFile().getVideoFileName());
            signedUrl.setSignedUrl(tmpUrl);
            signedUrl.setCreatedAt(new Date());
            signedUrl.setExpireAt(new Date(Instant.now().plus(16, ChronoUnit.HOURS).toEpochMilli()));
        });

        videoSignedUrlRepository.saveAll(signedUrls);
    }
}
