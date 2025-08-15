package com.tiktok.demo.mapper;

import org.mapstruct.Mapper;

import com.tiktok.demo.dto.request.HashtagRequest;
import com.tiktok.demo.dto.response.HashtagResponse;
import com.tiktok.demo.entity.Hashtag;

@Mapper(componentModel="spring")
public interface HashtagMapper {

    Hashtag toHashtag(HashtagRequest request);

    HashtagResponse toHashtagResponse(Hashtag hashtag);
}
