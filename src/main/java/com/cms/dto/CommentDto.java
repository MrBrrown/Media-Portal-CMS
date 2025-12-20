package com.cms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public class CommentDto {
    public static class CommentRequestDto {
        @NotNull(message = "Content ID is required")
        private Long contentId;

        @NotBlank(message = "Content type is required")
        private String contentType;

        @NotBlank(message = "Text is required")
        private String text;

        private String parentCommentId;

        public Long getContentId() {
            return contentId;
        }

        public void setContentId(Long contentId) {
            this.contentId = contentId;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getParentCommentId() {
            return parentCommentId;
        }

        public void setParentCommentId(String parentCommentId) {
            this.parentCommentId = parentCommentId;
        }
    }

    public static class CommentResponseDto {
        private String id;
        private Long contentId;
        private String contentType;
        private String text;
        private String author;
        private List<CommentResponseDto> replies;
        private LocalDateTime createdAt;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Long getContentId() {
            return contentId;
        }

        public void setContentId(Long contentId) {
            this.contentId = contentId;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public List<CommentResponseDto> getReplies() {
            return replies;
        }

        public void setReplies(List<CommentResponseDto> replies) {
            this.replies = replies;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }
}

