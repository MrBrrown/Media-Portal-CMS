package com.cms.service;

import com.cms.dto.ArticleDto;
import com.cms.exception.ResourceNotFoundException;
import com.cms.model.entity.Article;
import com.cms.model.entity.Role;
import com.cms.model.entity.User;
import com.cms.repository.ArticleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {
    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ArticleService articleService;

    private User testAuthor;
    private Article testArticle;

    @BeforeEach
    void setUp() {
        testAuthor = new User();
        testAuthor.setId(1L);
        testAuthor.setUsername("author");
        testAuthor.setEmail("author@example.com");
        testAuthor.setRole(Role.USER);

        testArticle = new Article();
        testArticle.setId(1L);
        testArticle.setTitle("Test Article");
        testArticle.setContent("Test Content");
        testArticle.setAuthor(testAuthor);
        testArticle.setPublicationDate(LocalDateTime.now());
        testArticle.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateArticle() {
        ArticleDto.ArticleRequestDto requestDto = new ArticleDto.ArticleRequestDto();
        requestDto.setTitle("New Article");
        requestDto.setContent("New Content");
        requestDto.setPublicationDate(LocalDateTime.now());

        when(userService.findById(1L)).thenReturn(testAuthor);
        when(articleRepository.save(any(Article.class))).thenReturn(testArticle);

        ArticleDto.ArticleResponseDto response = articleService.create(requestDto, 1L);

        assertNotNull(response);
        assertEquals("Test Article", response.getTitle());
        verify(articleRepository, times(1)).save(any(Article.class));
    }

    @Test
    void testGetArticleById() {
        when(articleRepository.findById(1L)).thenReturn(Optional.of(testArticle));

        ArticleDto.ArticleResponseDto response = articleService.getById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Article", response.getTitle());
    }

    @Test
    void testGetArticleByIdNotFound() {
        when(articleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> articleService.getById(1L));
    }

    @Test
    void testGetAllArticles() {
        Article article2 = new Article();
        article2.setId(2L);
        article2.setTitle("Article 2");
        article2.setContent("Content 2");
        article2.setAuthor(testAuthor);

        List<Article> articles = Arrays.asList(testArticle, article2);
        when(articleRepository.findAll()).thenReturn(articles);

        List<ArticleDto.ArticleResponseDto> response = articleService.getAll();

        assertNotNull(response);
        assertEquals(2, response.size());
    }

    @Test
    void testUpdateArticle() {
        ArticleDto.ArticleRequestDto requestDto = new ArticleDto.ArticleRequestDto();
        requestDto.setTitle("Updated Article");
        requestDto.setContent("Updated Content");

        when(articleRepository.findById(1L)).thenReturn(Optional.of(testArticle));
        when(articleRepository.save(any(Article.class))).thenReturn(testArticle);

        ArticleDto.ArticleResponseDto response = articleService.update(1L, requestDto);

        assertNotNull(response);
        verify(articleRepository, times(1)).save(any(Article.class));
    }

    @Test
    void testUpdateArticleNotFound() {
        ArticleDto.ArticleRequestDto requestDto = new ArticleDto.ArticleRequestDto();
        requestDto.setTitle("Updated Article");

        when(articleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> articleService.update(1L, requestDto));
    }

    @Test
    void testDeleteArticle() {
        when(articleRepository.existsById(1L)).thenReturn(true);
        doNothing().when(articleRepository).deleteById(1L);

        articleService.delete(1L);

        verify(articleRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteArticleNotFound() {
        when(articleRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> articleService.delete(1L));
        verify(articleRepository, never()).deleteById(any());
    }
}

