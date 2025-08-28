package com.tiktok.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tiktok.demo.dto.request.HashtagRequest;
import com.tiktok.demo.dto.response.HashtagResponse;
import com.tiktok.demo.entity.Hashtag;
import com.tiktok.demo.exception.AppException;
import com.tiktok.demo.exception.ErrorCode;
import com.tiktok.demo.mapper.HashtagMapper;
import com.tiktok.demo.repository.HashtagRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE, makeFinal=true)
public class HashtagService {
    HashtagRepository hashtagRepository;
    HashtagMapper hashtagMapper;

    public Hashtag handleCreateHashtag(String tag){
        return hashtagRepository.findByTag(tag)
            .map(h -> {
                h.setUseCount(h.getUseCount() + 1);
                return hashtagRepository.save(h);
            })
            .orElseGet(() -> {
                Hashtag newHashtag = Hashtag.builder()
                    .tag(tag)
                    .build();
                newHashtag.setUseCount(1);
                return hashtagRepository.save(newHashtag);
            });
    }

    public HashtagResponse createHashtag(HashtagRequest request){
        Hashtag hashtag = handleCreateHashtag(request.getTag());

        return hashtagMapper.toHashtagResponse(hashtag);
    }

    public HashtagResponse getHashtag(String tag){
        Hashtag hashtag = hashtagRepository.findByTag(tag).orElseThrow(() -> new AppException(ErrorCode.HASHTAG_NOT_EXISTED));
        return hashtagMapper.toHashtagResponse(hashtag);
    }

    public List<HashtagResponse> getHashtags(){
        return hashtagRepository.findAll().stream().map(hashtagMapper::toHashtagResponse).toList();
    }


}
