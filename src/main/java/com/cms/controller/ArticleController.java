package com.cms.controller;

import com.cms.dto.ArticleDto;
import com.cms.service.ArticleService;
import com.cms.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @Autowired
    private SecurityUtil securityUtil;

    @PostMapping
    public ResponseEntity<ArticleDto.ArticleResponseDto> create(
            @Valid @RequestBody ArticleDto.ArticleRequestDto requestDto,
            HttpServletRequest request) {
        if (!securityUtil.isAdmin()) {
            throw new com.cms.exception.BadRequestException("Only ADMIN users can create articles");
        }
        Long authorId = securityUtil.getCurrentUserId(request);
        if (authorId == null) {
            throw new com.cms.exception.BadRequestException("User not authenticated");
        }
        ArticleDto.ArticleResponseDto response = articleService.create(requestDto, authorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleDto.ArticleResponseDto> getById(@PathVariable Long id) {
        ArticleDto.ArticleResponseDto response = articleService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ArticleDto.ArticleResponseDto>> getAll() {
        List<ArticleDto.ArticleResponseDto> response = articleService.getAll();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleDto.ArticleResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody ArticleDto.ArticleRequestDto requestDto) {
        if (!securityUtil.isAdmin()) {
            throw new com.cms.exception.BadRequestException("Only ADMIN users can update articles");
        }
        ArticleDto.ArticleResponseDto response = articleService.update(id, requestDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!securityUtil.isAdmin()) {
            throw new com.cms.exception.BadRequestException("Only ADMIN users can delete articles");
        }
        articleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

