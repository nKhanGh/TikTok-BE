package com.tiktok.demo.mapper;

import org.mapstruct.Mapper;

import com.tiktok.demo.dto.request.MusicRequest;
import com.tiktok.demo.dto.response.MusicResponse;
import com.tiktok.demo.entity.Music;

@Mapper(componentModel="spring")
public interface MusicMapper {
    Music toMusic(MusicRequest request);
    MusicResponse toMusicResponse(Music music);
}
