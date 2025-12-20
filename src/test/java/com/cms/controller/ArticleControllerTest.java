package com.cms.controller;

import com.cms.dto.ArticleDto;
import com.cms.service.ArticleService;
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

@WebMvcTest(ArticleController.class)
class ArticleControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArticleService articleService;

    @MockBean
    private SecurityUtil securityUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void testCreateArticle() throws Exception {
        ArticleDto.ArticleRequestDto requestDto = new ArticleDto.ArticleRequestDto();
        requestDto.setTitle("Test Article");
        requestDto.setContent("Test Content");
        requestDto.setPublicationDate(LocalDateTime.now());

        ArticleDto.ArticleResponseDto responseDto = new ArticleDto.ArticleResponseDto();
        responseDto.setId(1L);
        responseDto.setTitle("Test Article");
        responseDto.setContent("Test Content");
        responseDto.setAuthorId(1L);
        responseDto.setAuthorUsername("testuser");

        when(securityUtil.getCurrentUserId(any())).thenReturn(1L);
        when(articleService.create(any(ArticleDto.ArticleRequestDto.class), eq(1L))).thenReturn(responseDto);

        mockMvc.perform(post("/api/articles")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Article"));
    }

    @Test
    @WithMockUser
    void testGetArticleById() throws Exception {
        ArticleDto.ArticleResponseDto responseDto = new ArticleDto.ArticleResponseDto();
        responseDto.setId(1L);
        responseDto.setTitle("Test Article");
        responseDto.setContent("Test Content");

        when(articleService.getById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/articles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Article"));
    }

    @Test
    @WithMockUser
    void testGetAllArticles() throws Exception {
        ArticleDto.ArticleResponseDto article1 = new ArticleDto.ArticleResponseDto();
        article1.setId(1L);
        article1.setTitle("Article 1");

        ArticleDto.ArticleResponseDto article2 = new ArticleDto.ArticleResponseDto();
        article2.setId(2L);
        article2.setTitle("Article 2");

        List<ArticleDto.ArticleResponseDto> articles = Arrays.asList(article1, article2);

        when(articleService.getAll()).thenReturn(articles);

        mockMvc.perform(get("/api/articles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    @WithMockUser
    void testUpdateArticle() throws Exception {
        ArticleDto.ArticleRequestDto requestDto = new ArticleDto.ArticleRequestDto();
        requestDto.setTitle("Updated Article");
        requestDto.setContent("Updated Content");

        ArticleDto.ArticleResponseDto responseDto = new ArticleDto.ArticleResponseDto();
        responseDto.setId(1L);
        responseDto.setTitle("Updated Article");
        responseDto.setContent("Updated Content");

        when(articleService.update(eq(1L), any(ArticleDto.ArticleRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/articles/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Article"));
    }

    @Test
    @WithMockUser
    void testDeleteArticle() throws Exception {
        mockMvc.perform(delete("/api/articles/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}

