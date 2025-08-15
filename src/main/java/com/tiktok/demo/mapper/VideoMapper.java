package com.tiktok.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.tiktok.demo.dto.request.VideoRequest;
import com.tiktok.demo.dto.response.VideoResponse;
import com.tiktok.demo.entity.Video;

@Mapper(componentModel="spring")
public interface  VideoMapper {
    @Mapping(target="hashtags", ignore=true)
    Video toVideo(VideoRequest request);

    VideoResponse toVideoResponse(Video video);
}
