package com.cms.service;

import com.cms.dto.VideoDto;
import com.cms.exception.ResourceNotFoundException;
import com.cms.model.entity.Video;
import com.cms.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VideoService {
    @Autowired
    private VideoRepository videoRepository;

    @Transactional
    public VideoDto.VideoResponseDto create(VideoDto.VideoRequestDto requestDto) {
        Video video = new Video();
        video.setTitle(requestDto.getTitle());
        video.setUrl(requestDto.getUrl());
        video.setDuration(requestDto.getDuration());

        video = videoRepository.save(video);
        return convertToDto(video);
    }

    public VideoDto.VideoResponseDto getById(Long id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Video not found with id: " + id));
        return convertToDto(video);
    }

    public List<VideoDto.VideoResponseDto> getAll() {
        return videoRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public VideoDto.VideoResponseDto update(Long id, VideoDto.VideoRequestDto requestDto) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Video not found with id: " + id));

        video.setTitle(requestDto.getTitle());
        video.setUrl(requestDto.getUrl());
        video.setDuration(requestDto.getDuration());

        video = videoRepository.save(video);
        return convertToDto(video);
    }

    @Transactional
    public void delete(Long id) {
        if (!videoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Video not found with id: " + id);
        }
        videoRepository.deleteById(id);
    }

    private VideoDto.VideoResponseDto convertToDto(Video video) {
        VideoDto.VideoResponseDto dto = new VideoDto.VideoResponseDto();
        dto.setId(video.getId());
        dto.setTitle(video.getTitle());
        dto.setUrl(video.getUrl());
        dto.setDuration(video.getDuration());
        dto.setCreatedAt(video.getCreatedAt());
        return dto;
    }
}

