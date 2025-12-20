package com.cms.service;

import com.cms.dto.ArticleDto;
import com.cms.exception.ResourceNotFoundException;
import com.cms.model.entity.Article;
import com.cms.model.entity.User;
import com.cms.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleService {
    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public ArticleDto.ArticleResponseDto create(ArticleDto.ArticleRequestDto requestDto, Long authorId) {
        User author = userService.findById(authorId);

        Article article = new Article();
        article.setTitle(requestDto.getTitle());
        article.setContent(requestDto.getContent());
        article.setAuthor(author);
        article.setPublicationDate(requestDto.getPublicationDate() != null
                ? requestDto.getPublicationDate()
                : LocalDateTime.now());

        article = articleRepository.save(article);
        return convertToDto(article);
    }

    public ArticleDto.ArticleResponseDto getById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + id));
        if (article.getAuthor() == null) {
            throw new ResourceNotFoundException("Article author not found");
        }
        return convertToDto(article);
    }

    public List<ArticleDto.ArticleResponseDto> getAll() {
        return articleRepository.findAll().stream()
                .filter(article -> article.getAuthor() != null)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ArticleDto.ArticleResponseDto update(Long id, ArticleDto.ArticleRequestDto requestDto) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + id));

        article.setTitle(requestDto.getTitle());
        article.setContent(requestDto.getContent());
        if (requestDto.getPublicationDate() != null) {
            article.setPublicationDate(requestDto.getPublicationDate());
        }

        article = articleRepository.save(article);
        return convertToDto(article);
    }

    @Transactional
    public void delete(Long id) {
        if (!articleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Article not found with id: " + id);
        }
        articleRepository.deleteById(id);
    }

    private ArticleDto.ArticleResponseDto convertToDto(Article article) {
        ArticleDto.ArticleResponseDto dto = new ArticleDto.ArticleResponseDto();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setContent(article.getContent());
        if (article.getAuthor() != null) {
            dto.setAuthorId(article.getAuthor().getId());
            dto.setAuthorUsername(article.getAuthor().getUsername());
        }
        dto.setPublicationDate(article.getPublicationDate());
        dto.setCreatedAt(article.getCreatedAt());
        return dto;
    }
}

