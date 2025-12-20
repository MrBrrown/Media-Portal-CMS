package com.cms.service;

import com.cms.dto.CommentDto;
import com.cms.exception.ResourceNotFoundException;
import com.cms.model.document.Comment;
import com.cms.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @org.springframework.transaction.annotation.Transactional
    public CommentDto.CommentResponseDto create(CommentDto.CommentRequestDto requestDto, String author) {
        Comment comment = new Comment();
        comment.setContentId(requestDto.getContentId());
        comment.setContentType(requestDto.getContentType());
        comment.setText(requestDto.getText());
        comment.setAuthor(author);

        if (requestDto.getParentCommentId() != null && !requestDto.getParentCommentId().isEmpty()) {
            Comment parentComment = commentRepository.findById(requestDto.getParentCommentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"));
            comment = commentRepository.save(comment);
            parentComment.getReplies().add(comment);
            commentRepository.save(parentComment);
            return convertToDto(comment);
        }

        comment = commentRepository.save(comment);
        return convertToDto(comment);
    }

    public CommentDto.CommentResponseDto getById(String id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));
        return convertToDto(comment);
    }

    public List<CommentDto.CommentResponseDto> getByContent(Long contentId, String contentType) {
        List<Comment> comments = commentRepository.findByContentIdAndContentType(contentId, contentType);
        return comments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @org.springframework.transaction.annotation.Transactional
    public CommentDto.CommentResponseDto update(String id, CommentDto.CommentRequestDto requestDto) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));

        comment.setText(requestDto.getText());

        comment = commentRepository.save(comment);
        return convertToDto(comment);
    }

    @org.springframework.transaction.annotation.Transactional
    public void delete(String id) {
        if (!commentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Comment not found with id: " + id);
        }
        commentRepository.deleteById(id);
    }

    private CommentDto.CommentResponseDto convertToDto(Comment comment) {
        CommentDto.CommentResponseDto dto = new CommentDto.CommentResponseDto();
        dto.setId(comment.getId());
        dto.setContentId(comment.getContentId());
        dto.setContentType(comment.getContentType());
        dto.setText(comment.getText());
        dto.setAuthor(comment.getAuthor());
        dto.setCreatedAt(comment.getCreatedAt());

        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            dto.setReplies(comment.getReplies().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}

