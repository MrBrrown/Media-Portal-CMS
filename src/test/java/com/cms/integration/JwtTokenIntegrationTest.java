package com.cms.integration;

import com.cms.model.document.JwtToken;
import com.cms.repository.JwtTokenRepository;
import com.cms.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class JwtTokenIntegrationTest {
    @Autowired
    private JwtTokenRepository jwtTokenRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenRepository.deleteAll();
    }

    @Test
    void testSaveToken() {
        String token = jwtTokenProvider.generateToken("testuser", 1L);
        Long userId = 1L;

        JwtToken jwtToken = new JwtToken();
        jwtToken.setToken(token);
        jwtToken.setUserId(userId);
        jwtToken.setExpiresAt(LocalDateTime.now().plusHours(24));
        jwtToken.setCreatedAt(LocalDateTime.now());

        JwtToken savedToken = jwtTokenRepository.save(jwtToken);

        assertNotNull(savedToken.getId());
        Optional<JwtToken> foundToken = jwtTokenRepository.findById(savedToken.getId());
        assertTrue(foundToken.isPresent());
        assertEquals(token, foundToken.get().getToken());
        assertEquals(userId, foundToken.get().getUserId());
    }

    @Test
    void testFindByToken() {
        String token = jwtTokenProvider.generateToken("testuser", 1L);

        JwtToken jwtToken = new JwtToken();
        jwtToken.setToken(token);
        jwtToken.setUserId(1L);
        jwtToken.setExpiresAt(LocalDateTime.now().plusHours(24));
        jwtToken.setCreatedAt(LocalDateTime.now());

        jwtTokenRepository.save(jwtToken);

        Optional<JwtToken> foundToken = jwtTokenRepository.findByToken(token);
        assertTrue(foundToken.isPresent());
        assertEquals(token, foundToken.get().getToken());
        assertEquals(1L, foundToken.get().getUserId());
    }

    @Test
    void testFindByUserId() {
        String token1 = jwtTokenProvider.generateToken("testuser", 1L);
        String token2 = jwtTokenProvider.generateToken("testuser", 1L);

        JwtToken jwtToken1 = new JwtToken();
        jwtToken1.setToken(token1);
        jwtToken1.setUserId(1L);
        jwtToken1.setExpiresAt(LocalDateTime.now().plusHours(24));
        jwtToken1.setCreatedAt(LocalDateTime.now());

        JwtToken jwtToken2 = new JwtToken();
        jwtToken2.setToken(token2);
        jwtToken2.setUserId(1L);
        jwtToken2.setExpiresAt(LocalDateTime.now().plusHours(24));
        jwtToken2.setCreatedAt(LocalDateTime.now());

        jwtTokenRepository.save(jwtToken1);
        jwtTokenRepository.save(jwtToken2);

        List<JwtToken> userTokens = jwtTokenRepository.findByUserId(1L);
        assertEquals(2, userTokens.size());
        assertTrue(userTokens.stream().anyMatch(t -> t.getToken().equals(token1)));
        assertTrue(userTokens.stream().anyMatch(t -> t.getToken().equals(token2)));
    }

    @Test
    void testDeleteByToken() {
        String token = jwtTokenProvider.generateToken("testuser", 1L);

        JwtToken jwtToken = new JwtToken();
        jwtToken.setToken(token);
        jwtToken.setUserId(1L);
        jwtToken.setExpiresAt(LocalDateTime.now().plusHours(24));
        jwtToken.setCreatedAt(LocalDateTime.now());

        JwtToken savedToken = jwtTokenRepository.save(jwtToken);
        String tokenId = savedToken.getId();

        assertTrue(jwtTokenRepository.existsById(tokenId));

        jwtTokenRepository.deleteByToken(token);

        assertFalse(jwtTokenRepository.existsById(tokenId));
        Optional<JwtToken> foundToken = jwtTokenRepository.findByToken(token);
        assertFalse(foundToken.isPresent());
    }

    @Test
    void testDeleteByUserId() {
        String token1 = jwtTokenProvider.generateToken("testuser", 1L);
        String token2 = jwtTokenProvider.generateToken("testuser", 1L);

        JwtToken jwtToken1 = new JwtToken();
        jwtToken1.setToken(token1);
        jwtToken1.setUserId(1L);
        jwtToken1.setExpiresAt(LocalDateTime.now().plusHours(24));
        jwtToken1.setCreatedAt(LocalDateTime.now());

        JwtToken jwtToken2 = new JwtToken();
        jwtToken2.setToken(token2);
        jwtToken2.setUserId(1L);
        jwtToken2.setExpiresAt(LocalDateTime.now().plusHours(24));
        jwtToken2.setCreatedAt(LocalDateTime.now());

        jwtTokenRepository.save(jwtToken1);
        jwtTokenRepository.save(jwtToken2);

        List<JwtToken> beforeDelete = jwtTokenRepository.findByUserId(1L);
        assertEquals(2, beforeDelete.size());

        jwtTokenRepository.deleteByUserId(1L);

        List<JwtToken> afterDelete = jwtTokenRepository.findByUserId(1L);
        assertEquals(0, afterDelete.size());
    }

    @Test
    void testDeleteByExpiresAtBefore() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expired = now.minusHours(1);
        LocalDateTime future = now.plusHours(24);

        JwtToken expiredToken = new JwtToken();
        expiredToken.setToken(jwtTokenProvider.generateToken("user1", 1L));
        expiredToken.setUserId(1L);
        expiredToken.setExpiresAt(expired);
        expiredToken.setCreatedAt(LocalDateTime.now());

        JwtToken validToken = new JwtToken();
        validToken.setToken(jwtTokenProvider.generateToken("user2", 2L));
        validToken.setUserId(2L);
        validToken.setExpiresAt(future);
        validToken.setCreatedAt(LocalDateTime.now());

        jwtTokenRepository.save(expiredToken);
        jwtTokenRepository.save(validToken);

        List<JwtToken> beforeCleanup = jwtTokenRepository.findAll();
        assertEquals(2, beforeCleanup.size());

        jwtTokenRepository.deleteByExpiresAtBefore(now);

        List<JwtToken> afterCleanup = jwtTokenRepository.findAll();
        assertEquals(1, afterCleanup.size());
        assertEquals(validToken.getToken(), afterCleanup.get(0).getToken());
    }

    @Test
    void testIsTokenValid() {
        String validToken = jwtTokenProvider.generateToken("testuser", 1L);

        JwtToken jwtToken = new JwtToken();
        jwtToken.setToken(validToken);
        jwtToken.setUserId(1L);
        jwtToken.setExpiresAt(LocalDateTime.now().plusHours(24));
        jwtToken.setCreatedAt(LocalDateTime.now());

        jwtTokenRepository.save(jwtToken);

        assertTrue(jwtTokenProvider.validateToken(validToken));
        assertTrue(jwtTokenRepository.findByToken(validToken).isPresent());

        jwtTokenRepository.deleteByToken(validToken);

        assertTrue(jwtTokenProvider.validateToken(validToken));
        assertFalse(jwtTokenRepository.findByToken(validToken).isPresent());
    }
}

