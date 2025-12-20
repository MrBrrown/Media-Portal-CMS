package com.cms.controller;

import com.cms.dto.PodcastDto;
import com.cms.service.PodcastService;
import com.cms.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/podcasts")
public class PodcastController {
    @Autowired
    private PodcastService podcastService;

    @Autowired
    private SecurityUtil securityUtil;

    @PostMapping
    public ResponseEntity<PodcastDto.PodcastResponseDto> create(@Valid @RequestBody PodcastDto.PodcastRequestDto requestDto) {
        if (!securityUtil.isAdmin()) {
            throw new com.cms.exception.BadRequestException("Only ADMIN users can create podcasts");
        }
        PodcastDto.PodcastResponseDto response = podcastService.create(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PodcastDto.PodcastResponseDto> getById(@PathVariable Long id) {
        PodcastDto.PodcastResponseDto response = podcastService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PodcastDto.PodcastResponseDto>> getAll() {
        List<PodcastDto.PodcastResponseDto> response = podcastService.getAll();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PodcastDto.PodcastResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody PodcastDto.PodcastRequestDto requestDto) {
        if (!securityUtil.isAdmin()) {
            throw new com.cms.exception.BadRequestException("Only ADMIN users can update podcasts");
        }
        PodcastDto.PodcastResponseDto response = podcastService.update(id, requestDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!securityUtil.isAdmin()) {
            throw new com.cms.exception.BadRequestException("Only ADMIN users can delete podcasts");
        }
        podcastService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

