package com.cms.integration;

import com.cms.model.document.Comment;
import com.cms.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
class CommentIntegrationTest {
    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
    }

    @Test
    void testCreateComment() {
        Comment comment = new Comment();
        comment.setContentId(1L);
        comment.setContentType("ARTICLE");
        comment.setText("Test comment");
        comment.setAuthor("testuser");
        comment.setCreatedAt(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);

        assertNotNull(savedComment.getId());
        Optional<Comment> foundComment = commentRepository.findById(savedComment.getId());
        assertTrue(foundComment.isPresent());
        assertEquals("Test comment", foundComment.get().getText());
        assertEquals(1L, foundComment.get().getContentId());
        assertEquals("ARTICLE", foundComment.get().getContentType());
        assertEquals("testuser", foundComment.get().getAuthor());
    }

    @Test
    void testCreateNestedComment() {
        Comment parentComment = new Comment();
        parentComment.setContentId(1L);
        parentComment.setContentType("ARTICLE");
        parentComment.setText("Parent comment");
        parentComment.setAuthor("user1");
        parentComment.setCreatedAt(LocalDateTime.now());

        Comment savedParent = commentRepository.save(parentComment);

        Comment replyComment = new Comment();
        replyComment.setContentId(1L);
        replyComment.setContentType("ARTICLE");
        replyComment.setText("Reply comment");
        replyComment.setAuthor("user2");
        replyComment.setCreatedAt(LocalDateTime.now());

        Comment savedReply = commentRepository.save(replyComment);

        savedParent.getReplies().add(savedReply);
        commentRepository.save(savedParent);

        Optional<Comment> foundParent = commentRepository.findById(savedParent.getId());
        assertTrue(foundParent.isPresent());
        assertEquals(1, foundParent.get().getReplies().size());
        assertEquals("Reply comment", foundParent.get().getReplies().get(0).getText());
    }

    @Test
    void testFindByContentIdAndContentType() {
        Comment comment1 = new Comment();
        comment1.setContentId(1L);
        comment1.setContentType("ARTICLE");
        comment1.setText("Comment 1");
        comment1.setAuthor("user1");
        comment1.setCreatedAt(LocalDateTime.now());

        Comment comment2 = new Comment();
        comment2.setContentId(1L);
        comment2.setContentType("ARTICLE");
        comment2.setText("Comment 2");
        comment2.setAuthor("user2");
        comment2.setCreatedAt(LocalDateTime.now());

        Comment comment3 = new Comment();
        comment3.setContentId(2L);
        comment3.setContentType("VIDEO");
        comment3.setText("Comment 3");
        comment3.setAuthor("user3");
        comment3.setCreatedAt(LocalDateTime.now());

        commentRepository.save(comment1);
        commentRepository.save(comment2);
        commentRepository.save(comment3);

        List<Comment> articleComments = commentRepository.findByContentIdAndContentType(1L, "ARTICLE");
        assertEquals(2, articleComments.size());
        assertTrue(articleComments.stream().anyMatch(c -> c.getText().equals("Comment 1")));
        assertTrue(articleComments.stream().anyMatch(c -> c.getText().equals("Comment 2")));

        List<Comment> videoComments = commentRepository.findByContentIdAndContentType(2L, "VIDEO");
        assertEquals(1, videoComments.size());
        assertEquals("Comment 3", videoComments.get(0).getText());
    }

    @Test
    void testUpdateComment() {
        Comment comment = new Comment();
        comment.setContentId(1L);
        comment.setContentType("ARTICLE");
        comment.setText("Original text");
        comment.setAuthor("testuser");
        comment.setCreatedAt(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        String commentId = savedComment.getId();

        savedComment.setText("Updated text");
        Comment updatedComment = commentRepository.save(savedComment);

        Optional<Comment> foundComment = commentRepository.findById(commentId);
        assertTrue(foundComment.isPresent());
        assertEquals("Updated text", foundComment.get().getText());
    }

    @Test
    void testDeleteComment() {
        Comment comment = new Comment();
        comment.setContentId(1L);
        comment.setContentType("ARTICLE");
        comment.setText("To delete");
        comment.setAuthor("testuser");
        comment.setCreatedAt(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        String commentId = savedComment.getId();

        assertTrue(commentRepository.existsById(commentId));

        commentRepository.deleteById(commentId);

        assertFalse(commentRepository.existsById(commentId));
        Optional<Comment> foundComment = commentRepository.findById(commentId);
        assertFalse(foundComment.isPresent());
    }

    @Test
    void testNestedCommentStructure() {
        Comment parent = new Comment();
        parent.setContentId(1L);
        parent.setContentType("ARTICLE");
        parent.setText("Parent");
        parent.setAuthor("user1");
        parent.setCreatedAt(LocalDateTime.now());

        Comment savedParent = commentRepository.save(parent);

        Comment reply1 = new Comment();
        reply1.setContentId(1L);
        reply1.setContentType("ARTICLE");
        reply1.setText("Reply 1");
        reply1.setAuthor("user2");
        reply1.setCreatedAt(LocalDateTime.now());

        Comment reply2 = new Comment();
        reply2.setContentId(1L);
        reply2.setContentType("ARTICLE");
        reply2.setText("Reply 2");
        reply2.setAuthor("user3");
        reply2.setCreatedAt(LocalDateTime.now());

        Comment savedReply1 = commentRepository.save(reply1);
        Comment savedReply2 = commentRepository.save(reply2);

        savedParent.getReplies().add(savedReply1);
        savedParent.getReplies().add(savedReply2);
        commentRepository.save(savedParent);

        Optional<Comment> foundParent = commentRepository.findById(savedParent.getId());
        assertTrue(foundParent.isPresent());
        assertEquals(2, foundParent.get().getReplies().size());
        assertTrue(foundParent.get().getReplies().stream().anyMatch(r -> r.getText().equals("Reply 1")));
        assertTrue(foundParent.get().getReplies().stream().anyMatch(r -> r.getText().equals("Reply 2")));
    }
}

