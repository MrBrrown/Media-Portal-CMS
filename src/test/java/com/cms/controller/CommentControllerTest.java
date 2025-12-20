package com.cms.controller;

import com.cms.dto.CommentDto;
import com.cms.service.CommentService;
import com.cms.util.SecurityUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @MockBean
    private SecurityUtil securityUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void testCreateComment() throws Exception {
        CommentDto.CommentRequestDto requestDto = new CommentDto.CommentRequestDto();
        requestDto.setContentId(1L);
        requestDto.setContentType("ARTICLE");
        requestDto.setText("Test comment");

        CommentDto.CommentResponseDto responseDto = new CommentDto.CommentResponseDto();
        responseDto.setId("comment-id");
        responseDto.setContentId(1L);
        responseDto.setContentType("ARTICLE");
        responseDto.setText("Test comment");
        responseDto.setAuthor("testuser");
        responseDto.setCreatedAt(LocalDateTime.now());

        when(securityUtil.getCurrentUsername()).thenReturn("testuser");
        when(commentService.create(any(CommentDto.CommentRequestDto.class), eq("testuser"))).thenReturn(responseDto);

        mockMvc.perform(post("/api/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("comment-id"))
                .andExpect(jsonPath("$.text").value("Test comment"));
    }

    @Test
    @WithMockUser
    void testGetCommentById() throws Exception {
        CommentDto.CommentResponseDto responseDto = new CommentDto.CommentResponseDto();
        responseDto.setId("comment-id");
        responseDto.setText("Test comment");

        when(commentService.getById("comment-id")).thenReturn(responseDto);

        mockMvc.perform(get("/api/comments/comment-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("comment-id"))
                .andExpect(jsonPath("$.text").value("Test comment"));
    }

    @Test
    @WithMockUser
    void testGetCommentsByContent() throws Exception {
        CommentDto.CommentResponseDto comment1 = new CommentDto.CommentResponseDto();
        comment1.setId("comment-1");
        comment1.setText("Comment 1");

        CommentDto.CommentResponseDto comment2 = new CommentDto.CommentResponseDto();
        comment2.setId("comment-2");
        comment2.setText("Comment 2");

        List<CommentDto.CommentResponseDto> comments = Arrays.asList(comment1, comment2);

        when(commentService.getByContent(1L, "ARTICLE")).thenReturn(comments);

        mockMvc.perform(get("/api/comments")
                        .param("contentId", "1")
                        .param("contentType", "ARTICLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser
    void testUpdateComment() throws Exception {
        CommentDto.CommentRequestDto requestDto = new CommentDto.CommentRequestDto();
        requestDto.setText("Updated comment");

        CommentDto.CommentResponseDto responseDto = new CommentDto.CommentResponseDto();
        responseDto.setId("comment-id");
        responseDto.setText("Updated comment");

        when(commentService.update(eq("comment-id"), any(CommentDto.CommentRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/comments/comment-id")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Updated comment"));
    }

    @Test
    @WithMockUser
    void testDeleteComment() throws Exception {
        mockMvc.perform(delete("/api/comments/comment-id")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}

