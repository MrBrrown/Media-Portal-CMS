package com.cms.controller;

import com.cms.dto.PodcastDto;
import com.cms.service.PodcastService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PodcastController.class)
class PodcastControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PodcastService podcastService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void testCreatePodcast() throws Exception {
        PodcastDto.PodcastRequestDto requestDto = new PodcastDto.PodcastRequestDto();
        requestDto.setTitle("Test Podcast");
        requestDto.setAudioUrl("https://example.com/podcast.mp3");
        requestDto.setEpisodes(Arrays.asList("Episode 1", "Episode 2"));

        PodcastDto.PodcastResponseDto responseDto = new PodcastDto.PodcastResponseDto();
        responseDto.setId(1L);
        responseDto.setTitle("Test Podcast");
        responseDto.setAudioUrl("https://example.com/podcast.mp3");
        responseDto.setEpisodes(Arrays.asList("Episode 1", "Episode 2"));

        when(podcastService.create(any(PodcastDto.PodcastRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/podcasts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Podcast"))
                .andExpect(jsonPath("$.episodes.length()").value(2));
    }

    @Test
    @WithMockUser
    void testGetPodcastById() throws Exception {
        PodcastDto.PodcastResponseDto responseDto = new PodcastDto.PodcastResponseDto();
        responseDto.setId(1L);
        responseDto.setTitle("Test Podcast");

        when(podcastService.getById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/podcasts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Podcast"));
    }

    @Test
    @WithMockUser
    void testGetAllPodcasts() throws Exception {
        PodcastDto.PodcastResponseDto podcast1 = new PodcastDto.PodcastResponseDto();
        podcast1.setId(1L);
        podcast1.setTitle("Podcast 1");

        PodcastDto.PodcastResponseDto podcast2 = new PodcastDto.PodcastResponseDto();
        podcast2.setId(2L);
        podcast2.setTitle("Podcast 2");

        List<PodcastDto.PodcastResponseDto> podcasts = Arrays.asList(podcast1, podcast2);

        when(podcastService.getAll()).thenReturn(podcasts);

        mockMvc.perform(get("/api/podcasts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser
    void testUpdatePodcast() throws Exception {
        PodcastDto.PodcastRequestDto requestDto = new PodcastDto.PodcastRequestDto();
        requestDto.setTitle("Updated Podcast");
        requestDto.setAudioUrl("https://example.com/updated.mp3");
        requestDto.setEpisodes(Arrays.asList("Episode 1"));

        PodcastDto.PodcastResponseDto responseDto = new PodcastDto.PodcastResponseDto();
        responseDto.setId(1L);
        responseDto.setTitle("Updated Podcast");

        when(podcastService.update(eq(1L), any(PodcastDto.PodcastRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/podcasts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Podcast"));
    }

    @Test
    @WithMockUser
    void testDeletePodcast() throws Exception {
        mockMvc.perform(delete("/api/podcasts/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}

