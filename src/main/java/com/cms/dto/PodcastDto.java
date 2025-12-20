package com.cms.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.time.LocalDateTime;

public class PodcastDto {
    public static class PodcastRequestDto {
        @NotBlank(message = "Title is required")
        private String title;

        @NotBlank(message = "Audio URL is required")
        private String audioUrl;

        private List<String> episodes;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAudioUrl() {
            return audioUrl;
        }

        public void setAudioUrl(String audioUrl) {
            this.audioUrl = audioUrl;
        }

        public List<String> getEpisodes() {
            return episodes;
        }

        public void setEpisodes(List<String> episodes) {
            this.episodes = episodes;
        }
    }

    public static class PodcastResponseDto {
        private Long id;
        private String title;
        private String audioUrl;
        private List<String> episodes;
        private LocalDateTime createdAt;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAudioUrl() {
            return audioUrl;
        }

        public void setAudioUrl(String audioUrl) {
            this.audioUrl = audioUrl;
        }

        public List<String> getEpisodes() {
            return episodes;
        }

        public void setEpisodes(List<String> episodes) {
            this.episodes = episodes;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }
}

