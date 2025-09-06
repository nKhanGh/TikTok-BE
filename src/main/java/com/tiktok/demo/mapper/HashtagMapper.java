package com.tiktok.demo.mapper;

import org.mapstruct.Mapper;

import com.tiktok.demo.dto.response.HashtagResponse;
import com.tiktok.demo.entity.Hashtag;

@Mapper(componentModel="spring")
public interface HashtagMapper {


    HashtagResponse toHashtagResponse(Hashtag hashtag);
}
