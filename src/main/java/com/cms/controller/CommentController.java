package com.cms.controller;

import com.cms.dto.CommentDto;
import com.cms.service.CommentService;
import com.cms.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private SecurityUtil securityUtil;

    @PostMapping
    public ResponseEntity<CommentDto.CommentResponseDto> create(
            @Valid @RequestBody CommentDto.CommentRequestDto requestDto,
            HttpServletRequest request) {
        String author = securityUtil.getCurrentUsername();
        if (author == null) {
            throw new com.cms.exception.BadRequestException("User not authenticated");
        }
        CommentDto.CommentResponseDto response = commentService.create(requestDto, author);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDto.CommentResponseDto> getById(@PathVariable String id) {
        CommentDto.CommentResponseDto response = commentService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CommentDto.CommentResponseDto>> getByContent(
            @RequestParam Long contentId,
            @RequestParam String contentType) {
        List<CommentDto.CommentResponseDto> response = commentService.getByContent(contentId, contentType);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentDto.CommentResponseDto> update(
            @PathVariable String id,
            @Valid @RequestBody CommentDto.CommentRequestDto requestDto) {
        CommentDto.CommentResponseDto response = commentService.update(id, requestDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        commentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

