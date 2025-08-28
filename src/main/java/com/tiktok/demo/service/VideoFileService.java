package com.tiktok.demo.service;

import java.io.IOException;
import java.time.Duration;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Service
@RequiredArgsConstructor
@FieldDefaults(level =AccessLevel.PRIVATE, makeFinal=true)
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
            throw new RuntimeException("Bucket not found.");
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
            .signatureDuration(Duration.ofHours(1))
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


}
