package com.tiktok.demo.service;

import java.util.Date;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.tiktok.demo.dto.request.MusicRequest;
import com.tiktok.demo.dto.response.MusicResponse;
import com.tiktok.demo.entity.Music;
import com.tiktok.demo.entity.User;
import com.tiktok.demo.exception.AppException;
import com.tiktok.demo.exception.ErrorCode;
import com.tiktok.demo.mapper.MusicMapper;
import com.tiktok.demo.repository.MusicRepository;
import com.tiktok.demo.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal=true)
@Slf4j
public class MusicService {
    MusicRepository musicRepository;
    UserRepository userRepository;
    MusicMapper musicMapper;

    public MusicResponse addMusic(MusicRequest request){
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Music music = musicMapper.toMusic(request);

        music.setUserPost(user);
        music.setCreateAt(new Date());
        musicRepository.save(music);

        return musicMapper.toMusicResponse(music);
    }

    public void deleteMusic(String id){
        var music = musicRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.MUSIC_NOT_EXISTED));
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var username = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ADMIN_DELETE_VIDEO"));
        User user = music.getUserPost();

        if(!username.equals(user.getUsername()) && !isAdmin)
            throw new AppException(ErrorCode.UNAUTHORIZED);

        musicRepository.deleteById(id);
    }

    public List<MusicResponse> getMusics(){
        return musicRepository.findAll().stream().map(musicMapper::toMusicResponse).toList();
    }

    public MusicResponse getMusic(String id){
        Music music = musicRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.MUSIC_NOT_EXISTED));
        return musicMapper.toMusicResponse(music);
    }
}
