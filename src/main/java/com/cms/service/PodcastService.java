package com.cms.service;

import com.cms.dto.PodcastDto;
import com.cms.exception.ResourceNotFoundException;
import com.cms.model.entity.Podcast;
import com.cms.repository.PodcastRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PodcastService {
    @Autowired
    private PodcastRepository podcastRepository;

    @Transactional
    public PodcastDto.PodcastResponseDto create(PodcastDto.PodcastRequestDto requestDto) {
        Podcast podcast = new Podcast();
        podcast.setTitle(requestDto.getTitle());
        podcast.setAudioUrl(requestDto.getAudioUrl());
        podcast.setEpisodes(requestDto.getEpisodes() != null
                ? new ArrayList<>(requestDto.getEpisodes())
                : new ArrayList<>());

        podcast = podcastRepository.save(podcast);
        return convertToDto(podcast);
    }

    public PodcastDto.PodcastResponseDto getById(Long id) {
        Podcast podcast = podcastRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Podcast not found with id: " + id));
        return convertToDto(podcast);
    }

    public List<PodcastDto.PodcastResponseDto> getAll() {
        return podcastRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public PodcastDto.PodcastResponseDto update(Long id, PodcastDto.PodcastRequestDto requestDto) {
        Podcast podcast = podcastRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Podcast not found with id: " + id));

        podcast.setTitle(requestDto.getTitle());
        podcast.setAudioUrl(requestDto.getAudioUrl());
        podcast.setEpisodes(requestDto.getEpisodes() != null
                ? new ArrayList<>(requestDto.getEpisodes())
                : new ArrayList<>());

        podcast = podcastRepository.save(podcast);
        return convertToDto(podcast);
    }

    @Transactional
    public void delete(Long id) {
        if (!podcastRepository.existsById(id)) {
            throw new ResourceNotFoundException("Podcast not found with id: " + id);
        }
        podcastRepository.deleteById(id);
    }

    private PodcastDto.PodcastResponseDto convertToDto(Podcast podcast) {
        PodcastDto.PodcastResponseDto dto = new PodcastDto.PodcastResponseDto();
        dto.setId(podcast.getId());
        dto.setTitle(podcast.getTitle());
        dto.setAudioUrl(podcast.getAudioUrl());
        dto.setEpisodes(podcast.getEpisodes());
        dto.setCreatedAt(podcast.getCreatedAt());
        return dto;
    }
}

