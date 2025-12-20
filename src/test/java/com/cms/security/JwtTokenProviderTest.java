package com.cms.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {
    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    private String testToken;
    private String testUsername = "testuser";
    private Long testUserId = 1L;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", 
                "cms-jwt-secret-key-for-media-portal-application-2024-test");
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpiration", 86400000L);
        testToken = jwtTokenProvider.generateToken(testUsername, testUserId);
    }

    @Test
    void testGenerateToken() {
        assertNotNull(testToken);
        assertFalse(testToken.isEmpty());
    }

    @Test
    void testGetUsernameFromToken() {
        String username = jwtTokenProvider.getUsernameFromToken(testToken);
        assertEquals(testUsername, username);
    }

    @Test
    void testGetUserIdFromToken() {
        Long userId = jwtTokenProvider.getUserIdFromToken(testToken);
        assertEquals(testUserId, userId);
    }

    @Test
    void testValidateToken() {
        assertTrue(jwtTokenProvider.validateToken(testToken));
    }

    @Test
    void testValidateInvalidToken() {
        String invalidToken = "invalid.token.here";
        assertFalse(jwtTokenProvider.validateToken(invalidToken));
    }

    @Test
    void testGetExpirationDateFromToken() {
        assertNotNull(jwtTokenProvider.getExpirationDateFromToken(testToken));
    }
}

