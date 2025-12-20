package com.cms.controller;

import com.cms.dto.VideoDto;
import com.cms.service.VideoService;
import com.cms.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/videos")
public class VideoController {
    @Autowired
    private VideoService videoService;

    @Autowired
    private SecurityUtil securityUtil;

    @PostMapping
    public ResponseEntity<VideoDto.VideoResponseDto> create(@Valid @RequestBody VideoDto.VideoRequestDto requestDto) {
        if (!securityUtil.isAdmin()) {
            throw new com.cms.exception.BadRequestException("Only ADMIN users can create videos");
        }
        VideoDto.VideoResponseDto response = videoService.create(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VideoDto.VideoResponseDto> getById(@PathVariable Long id) {
        VideoDto.VideoResponseDto response = videoService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<VideoDto.VideoResponseDto>> getAll() {
        List<VideoDto.VideoResponseDto> response = videoService.getAll();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VideoDto.VideoResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody VideoDto.VideoRequestDto requestDto) {
        if (!securityUtil.isAdmin()) {
            throw new com.cms.exception.BadRequestException("Only ADMIN users can update videos");
        }
        VideoDto.VideoResponseDto response = videoService.update(id, requestDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!securityUtil.isAdmin()) {
            throw new com.cms.exception.BadRequestException("Only ADMIN users can delete videos");
        }
        videoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

