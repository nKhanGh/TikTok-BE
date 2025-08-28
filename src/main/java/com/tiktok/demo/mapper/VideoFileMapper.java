package com.tiktok.demo.mapper;

import org.mapstruct.Mapper;

import com.tiktok.demo.dto.response.VideoFileResponse;
import com.tiktok.demo.entity.VideoFile;

@Mapper(componentModel="spring")
public interface VideoFileMapper {
    VideoFileResponse toVideoFileResponse(VideoFile videoFile);
}
