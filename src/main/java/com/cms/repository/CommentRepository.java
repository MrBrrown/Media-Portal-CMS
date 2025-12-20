package com.cms.repository;

import com.cms.model.document.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findByContentIdAndContentType(Long contentId, String contentType);
    void deleteByContentIdAndContentType(Long contentId, String contentType);
}

