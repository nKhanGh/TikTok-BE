package com.tiktok.demo.mapper;

import org.mapstruct.Mapper;

import com.tiktok.demo.dto.response.VideoResponse;
import com.tiktok.demo.entity.Video;

@Mapper(componentModel="spring")
public interface  VideoMapper {

    VideoResponse toVideoResponse(Video video);
}
