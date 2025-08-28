package com.tiktok.demo.service;

import java.io.IOException;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.backblaze.b2.client.B2ListFileNamesIterable;
import com.backblaze.b2.client.B2ListFilesIterable;
import com.backblaze.b2.client.B2StorageClient;
import com.backblaze.b2.client.contentSources.B2ByteArrayContentSource;
import com.backblaze.b2.client.contentSources.B2ContentSource;
import com.backblaze.b2.client.exceptions.B2Exception;
import com.backblaze.b2.client.structures.B2Bucket;
import com.backblaze.b2.client.structures.B2DeleteFileVersionRequest;
import com.backblaze.b2.client.structures.B2ListFileNamesRequest;
import com.backblaze.b2.client.structures.B2ListFileNamesResponse;
import com.backblaze.b2.client.structures.B2UploadFileRequest;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Service
@RequiredArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE, makeFinal=true)
@Slf4j
public class ImageService {

    @NonFinal
    @Value("${b2.imageBucketName}")
    String bucketName;

    static String endPoint = "https://s3.eu-central-003.backblazeb2.com";

    B2StorageClient b2StorageClient;

    public String[] uploadImage(MultipartFile imageFile) throws B2Exception, IOException{
        String filename = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
        B2Bucket bucket = b2StorageClient.getBucketOrNullByName(bucketName);
        if (bucket == null) {
            throw new RuntimeException("Bucket not found.");
        }

        B2ContentSource source = B2ByteArrayContentSource.build(imageFile.getBytes());
        B2UploadFileRequest request = B2UploadFileRequest
            .builder(bucket.getBucketId(), filename, imageFile.getContentType(), source)
            .build();
        String fileId = b2StorageClient.uploadSmallFile(request).getFileId();
        String fileUrl = endPoint + "/" + bucketName + "/" + filename;
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
