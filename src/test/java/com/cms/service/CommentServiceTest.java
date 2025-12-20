package com.cms.service;

import com.cms.dto.CommentDto;
import com.cms.exception.ResourceNotFoundException;
import com.cms.model.document.Comment;
import com.cms.repository.CommentRepository;
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
class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    private Comment testComment;

    @BeforeEach
    void setUp() {
        testComment = new Comment();
        testComment.setId("comment-id");
        testComment.setContentId(1L);
        testComment.setContentType("ARTICLE");
        testComment.setText("Test comment");
        testComment.setAuthor("testuser");
        testComment.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateComment() {
        CommentDto.CommentRequestDto requestDto = new CommentDto.CommentRequestDto();
        requestDto.setContentId(1L);
        requestDto.setContentType("ARTICLE");
        requestDto.setText("New comment");

        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

        CommentDto.CommentResponseDto response = commentService.create(requestDto, "testuser");

        assertNotNull(response);
        assertEquals("comment-id", response.getId());
        assertEquals("Test comment", response.getText());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void testCreateNestedComment() {
        Comment parentComment = new Comment();
        parentComment.setId("parent-id");
        parentComment.setContentId(1L);
        parentComment.setContentType("ARTICLE");
        parentComment.setText("Parent comment");

        CommentDto.CommentRequestDto requestDto = new CommentDto.CommentRequestDto();
        requestDto.setContentId(1L);
        requestDto.setContentType("ARTICLE");
        requestDto.setText("Reply comment");
        requestDto.setParentCommentId("parent-id");

        when(commentRepository.findById("parent-id")).thenReturn(Optional.of(parentComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(parentComment);

        CommentDto.CommentResponseDto response = commentService.create(requestDto, "testuser");

        assertNotNull(response);
        verify(commentRepository, times(1)).findById("parent-id");
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void testGetCommentById() {
        when(commentRepository.findById("comment-id")).thenReturn(Optional.of(testComment));

        CommentDto.CommentResponseDto response = commentService.getById("comment-id");

        assertNotNull(response);
        assertEquals("comment-id", response.getId());
        assertEquals("Test comment", response.getText());
    }

    @Test
    void testGetCommentByIdNotFound() {
        when(commentRepository.findById("comment-id")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.getById("comment-id"));
    }

    @Test
    void testGetCommentsByContent() {
        Comment comment2 = new Comment();
        comment2.setId("comment-2");
        comment2.setContentId(1L);
        comment2.setContentType("ARTICLE");
        comment2.setText("Comment 2");

        List<Comment> comments = Arrays.asList(testComment, comment2);
        when(commentRepository.findByContentIdAndContentType(1L, "ARTICLE")).thenReturn(comments);

        List<CommentDto.CommentResponseDto> response = commentService.getByContent(1L, "ARTICLE");

        assertNotNull(response);
        assertEquals(2, response.size());
    }

    @Test
    void testUpdateComment() {
        CommentDto.CommentRequestDto requestDto = new CommentDto.CommentRequestDto();
        requestDto.setText("Updated comment");

        when(commentRepository.findById("comment-id")).thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

        CommentDto.CommentResponseDto response = commentService.update("comment-id", requestDto);

        assertNotNull(response);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void testUpdateCommentNotFound() {
        CommentDto.CommentRequestDto requestDto = new CommentDto.CommentRequestDto();
        requestDto.setText("Updated comment");

        when(commentRepository.findById("comment-id")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.update("comment-id", requestDto));
    }

    @Test
    void testDeleteComment() {
        when(commentRepository.existsById("comment-id")).thenReturn(true);
        doNothing().when(commentRepository).deleteById("comment-id");

        commentService.delete("comment-id");

        verify(commentRepository, times(1)).deleteById("comment-id");
    }

    @Test
    void testDeleteCommentNotFound() {
        when(commentRepository.existsById("comment-id")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> commentService.delete("comment-id"));
        verify(commentRepository, never()).deleteById(any());
    }
}

