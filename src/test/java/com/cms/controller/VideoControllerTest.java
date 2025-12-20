package com.cms.controller;

import com.cms.dto.VideoDto;
import com.cms.service.VideoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VideoController.class)
class VideoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VideoService videoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void testCreateVideo() throws Exception {
        VideoDto.VideoRequestDto requestDto = new VideoDto.VideoRequestDto();
        requestDto.setTitle("Test Video");
        requestDto.setUrl("https://example.com/video.mp4");
        requestDto.setDuration(Duration.ofHours(1).plusMinutes(30));

        VideoDto.VideoResponseDto responseDto = new VideoDto.VideoResponseDto();
        responseDto.setId(1L);
        responseDto.setTitle("Test Video");
        responseDto.setUrl("https://example.com/video.mp4");
        responseDto.setDuration(Duration.ofHours(1).plusMinutes(30));

        when(videoService.create(any(VideoDto.VideoRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/videos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Video"));
    }

    @Test
    @WithMockUser
    void testGetVideoById() throws Exception {
        VideoDto.VideoResponseDto responseDto = new VideoDto.VideoResponseDto();
        responseDto.setId(1L);
        responseDto.setTitle("Test Video");

        when(videoService.getById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/videos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Video"));
    }

    @Test
    @WithMockUser
    void testGetAllVideos() throws Exception {
        VideoDto.VideoResponseDto video1 = new VideoDto.VideoResponseDto();
        video1.setId(1L);
        video1.setTitle("Video 1");

        VideoDto.VideoResponseDto video2 = new VideoDto.VideoResponseDto();
        video2.setId(2L);
        video2.setTitle("Video 2");

        List<VideoDto.VideoResponseDto> videos = Arrays.asList(video1, video2);

        when(videoService.getAll()).thenReturn(videos);

        mockMvc.perform(get("/api/videos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser
    void testUpdateVideo() throws Exception {
        VideoDto.VideoRequestDto requestDto = new VideoDto.VideoRequestDto();
        requestDto.setTitle("Updated Video");
        requestDto.setUrl("https://example.com/updated.mp4");
        requestDto.setDuration(Duration.ofHours(2));

        VideoDto.VideoResponseDto responseDto = new VideoDto.VideoResponseDto();
        responseDto.setId(1L);
        responseDto.setTitle("Updated Video");

        when(videoService.update(eq(1L), any(VideoDto.VideoRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/videos/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Video"));
    }

    @Test
    @WithMockUser
    void testDeleteVideo() throws Exception {
        mockMvc.perform(delete("/api/videos/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}

