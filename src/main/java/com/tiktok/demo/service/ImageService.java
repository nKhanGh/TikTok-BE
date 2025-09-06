package com.tiktok.demo.service;

import java.io.IOException;

import com.tiktok.demo.exception.AppException;
import com.tiktok.demo.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.backblaze.b2.client.B2StorageClient;
import com.backblaze.b2.client.contentSources.B2ByteArrayContentSource;
import com.backblaze.b2.client.contentSources.B2ContentSource;
import com.backblaze.b2.client.exceptions.B2Exception;
import com.backblaze.b2.client.structures.B2Bucket;
import com.backblaze.b2.client.structures.B2UploadFileRequest;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE, makeFinal=true)
@Slf4j
public class ImageService {

    @NonFinal
    @Value("${b2.imageBucketName}")
    String bucketName;

    static String endPoint = "https://f003.backblazeb2.com/file";

    B2StorageClient b2StorageClient;

    public String[] uploadImage(MultipartFile imageFile) throws B2Exception, IOException{
        log.info("kHang");
        String originalFilename = imageFile.getOriginalFilename();
        if(originalFilename == null)
            throw new AppException(ErrorCode.IMAGE_ERROR);
        String filename = System.currentTimeMillis() + "_" 
            + originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");
        log.info("kHang2");
        B2Bucket bucket = b2StorageClient.getBucketOrNullByName(bucketName);
        if (bucket == null) {
            throw new AppException(ErrorCode.BUCKET_NOT_FOUND);
        }
        log.info("kHang3");

        B2ContentSource source = B2ByteArrayContentSource.build(imageFile.getBytes());
        B2UploadFileRequest request = B2UploadFileRequest
            .builder(bucket.getBucketId(), filename, imageFile.getContentType(), source)
            .build();
        log.info("kHang4");
        String fileId = b2StorageClient.uploadSmallFile(request).getFileId();
        log.info("kHang5");
        String fileUrl = endPoint + "/" + bucketName + "/" + filename;
        log.info("kHang6");
        return new String[]{fileUrl, fileId};
    }

    public String getImage(String url){
       return url;
    }

    public void deleteImage(String url, String fileId) throws B2Exception{
        int index = url.indexOf('/', 50);
        String filename = url.substring(index + 1);
        b2StorageClient.deleteFileVersion(filename, fileId);
    }
}
