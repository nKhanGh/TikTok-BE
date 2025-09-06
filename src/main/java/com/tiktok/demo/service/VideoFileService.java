package com.tiktok.demo.service;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.backblaze.b2.client.B2StorageClient;
import com.backblaze.b2.client.contentSources.B2ByteArrayContentSource;
import com.backblaze.b2.client.contentSources.B2ContentSource;
import com.backblaze.b2.client.exceptions.B2Exception;
import com.backblaze.b2.client.structures.B2Bucket;
import com.backblaze.b2.client.structures.B2UploadFileRequest;
import com.tiktok.demo.dto.response.VideoFileResponse;
import com.tiktok.demo.entity.VideoFile;
import com.tiktok.demo.exception.AppException;
import com.tiktok.demo.exception.ErrorCode;
import com.tiktok.demo.mapper.VideoFileMapper;
import com.tiktok.demo.repository.VideoFileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Service
@RequiredArgsConstructor
@FieldDefaults(level =AccessLevel.PRIVATE, makeFinal=true)
@Slf4j
public class VideoFileService {


    VideoFileRepository videoFileRepository;

    VideoFileMapper videoFileMapper;

    @NonFinal
    @Value("${b2.videoBucketName}")
    String bucketName;

    B2StorageClient b2StorageClient;

    S3Presigner s3Presigner;


    public VideoFileResponse uploadVideo(MultipartFile videoFile) throws B2Exception, IOException {
        String fileName = System.currentTimeMillis() + "_" + videoFile.getOriginalFilename();
        B2Bucket bucket = b2StorageClient.getBucketOrNullByName(bucketName);
        if (bucket == null) {
            throw new AppException(ErrorCode.BUCKET_NOT_FOUND);
        }
        B2ContentSource source = B2ByteArrayContentSource.build(videoFile.getBytes());
        B2UploadFileRequest request = B2UploadFileRequest
                .builder(bucket.getBucketId(), fileName, videoFile.getContentType(), source).build();
        String fileId = b2StorageClient.uploadSmallFile(request).getFileId();
        VideoFile videoFileEntity = VideoFile.builder()
            .videoFileId(fileId)
            .videoFileName(fileName)
            .uploadAt(new Date())
            .isPosted(false)
            .build();
        videoFileRepository.save(videoFileEntity);
        return videoFileMapper.toVideoFileResponse(videoFileEntity);
    }

    public String getVideoUrl(String fileName){
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofHours(10))
            .getObjectRequest(gor -> gor
                .bucket(bucketName)
                .key(fileName)
                .build())
            .build();
        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    public VideoFile getVideo(String id){
        VideoFile videoFile = videoFileRepository.findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.VIDEO_NOT_EXISTED));
        videoFile.setPosted(true);
        return videoFileRepository.save(videoFile);
    }

    public void deleteVideo(String fileName, String fileId) throws B2Exception{
        b2StorageClient.deleteFileVersion(fileName, fileId);
    }

    @Transactional
    public void deleteVideoScheduler(){
        List<VideoFile> videoFiles = videoFileRepository
            .findByIsPostedFalseAndUploadAtBefore(Date.from(Instant.now().minus(1, ChronoUnit.HOURS)));

        List<String> idsToDelete = new ArrayList<>();
        
        videoFiles.forEach(videoFile -> {
            try {
                deleteVideo(videoFile.getVideoFileName(), videoFile.getVideoFileId());
                idsToDelete.add(videoFile.getVideoFileId());
            } catch (B2Exception e) {
                log.error("Failed to delete from B2: {}", videoFile.getVideoFileName(), e);
            }
        });

        if(!idsToDelete.isEmpty())
            videoFileRepository.deleteAllById(idsToDelete);
    }

}
