package com.cms.integration;

import com.cms.model.entity.Article;
import com.cms.model.entity.Role;
import com.cms.model.entity.User;
import com.cms.repository.ArticleRepository;
import com.cms.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ArticleIntegrationTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    private User testAuthor;

    @BeforeEach
    void setUp() {
        testAuthor = new User();
        testAuthor.setUsername("testauthor");
        testAuthor.setEmail("author@test.com");
        testAuthor.setPassword("password123");
        testAuthor.setRole(Role.USER);
        testAuthor = entityManager.persistAndFlush(testAuthor);
    }

    @Test
    void testCreateArticle() {
        Article article = new Article();
        article.setTitle("Test Article");
        article.setContent("Test Content");
        article.setAuthor(testAuthor);
        article.setPublicationDate(LocalDateTime.now());

        Article savedArticle = articleRepository.save(article);
        entityManager.flush();
        entityManager.clear();

        assertNotNull(savedArticle.getId());
        Optional<Article> foundArticle = articleRepository.findById(savedArticle.getId());
        assertTrue(foundArticle.isPresent());
        assertEquals("Test Article", foundArticle.get().getTitle());
        assertEquals("Test Content", foundArticle.get().getContent());
        assertEquals(testAuthor.getId(), foundArticle.get().getAuthor().getId());
    }

    @Test
    void testFindArticleById() {
        Article article = new Article();
        article.setTitle("Find Test Article");
        article.setContent("Find Test Content");
        article.setAuthor(testAuthor);
        article.setPublicationDate(LocalDateTime.now());

        Article savedArticle = articleRepository.save(article);
        entityManager.flush();
        entityManager.clear();

        Optional<Article> foundArticle = articleRepository.findById(savedArticle.getId());
        assertTrue(foundArticle.isPresent());
        assertEquals(savedArticle.getId(), foundArticle.get().getId());
        assertEquals("Find Test Article", foundArticle.get().getTitle());
    }

    @Test
    void testFindAllArticles() {
        Article article1 = new Article();
        article1.setTitle("Article 1");
        article1.setContent("Content 1");
        article1.setAuthor(testAuthor);
        article1.setPublicationDate(LocalDateTime.now());

        Article article2 = new Article();
        article2.setTitle("Article 2");
        article2.setContent("Content 2");
        article2.setAuthor(testAuthor);
        article2.setPublicationDate(LocalDateTime.now());

        articleRepository.save(article1);
        articleRepository.save(article2);
        entityManager.flush();
        entityManager.clear();

        List<Article> articles = articleRepository.findAll();
        assertTrue(articles.size() >= 2);
        assertTrue(articles.stream().anyMatch(a -> a.getTitle().equals("Article 1")));
        assertTrue(articles.stream().anyMatch(a -> a.getTitle().equals("Article 2")));
    }

    @Test
    void testUpdateArticle() {
        Article article = new Article();
        article.setTitle("Original Title");
        article.setContent("Original Content");
        article.setAuthor(testAuthor);
        article.setPublicationDate(LocalDateTime.now());

        Article savedArticle = articleRepository.save(article);
        entityManager.flush();
        entityManager.clear();

        savedArticle.setTitle("Updated Title");
        savedArticle.setContent("Updated Content");
        Article updatedArticle = articleRepository.save(savedArticle);
        entityManager.flush();
        entityManager.clear();

        Optional<Article> foundArticle = articleRepository.findById(updatedArticle.getId());
        assertTrue(foundArticle.isPresent());
        assertEquals("Updated Title", foundArticle.get().getTitle());
        assertEquals("Updated Content", foundArticle.get().getContent());
    }

    @Test
    void testDeleteArticle() {
        Article article = new Article();
        article.setTitle("To Delete");
        article.setContent("Content to delete");
        article.setAuthor(testAuthor);
        article.setPublicationDate(LocalDateTime.now());

        Article savedArticle = articleRepository.save(article);
        Long articleId = savedArticle.getId();
        entityManager.flush();
        entityManager.clear();

        assertTrue(articleRepository.existsById(articleId));

        articleRepository.deleteById(articleId);
        entityManager.flush();
        entityManager.clear();

        assertFalse(articleRepository.existsById(articleId));
        Optional<Article> foundArticle = articleRepository.findById(articleId);
        assertFalse(foundArticle.isPresent());
    }
}

