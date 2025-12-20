package com.cms.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class ArticleDto {
    public static class ArticleRequestDto {
        @NotBlank(message = "Title is required")
        private String title;

        @NotBlank(message = "Content is required")
        private String content;

        private LocalDateTime publicationDate;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public LocalDateTime getPublicationDate() {
            return publicationDate;
        }

        public void setPublicationDate(LocalDateTime publicationDate) {
            this.publicationDate = publicationDate;
        }
    }

    public static class ArticleResponseDto {
        private Long id;
        private String title;
        private String content;
        private Long authorId;
        private String authorUsername;
        private LocalDateTime publicationDate;
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

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public Long getAuthorId() {
            return authorId;
        }

        public void setAuthorId(Long authorId) {
            this.authorId = authorId;
        }

        public String getAuthorUsername() {
            return authorUsername;
        }

        public void setAuthorUsername(String authorUsername) {
            this.authorUsername = authorUsername;
        }

        public LocalDateTime getPublicationDate() {
            return publicationDate;
        }

        public void setPublicationDate(LocalDateTime publicationDate) {
            this.publicationDate = publicationDate;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }
}

