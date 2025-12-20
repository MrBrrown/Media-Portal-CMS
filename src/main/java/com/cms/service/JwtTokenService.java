package com.cms.service;

import com.cms.model.document.JwtToken;
import com.cms.repository.JwtTokenRepository;
import com.cms.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Service
public class JwtTokenService {
    @Autowired
    private JwtTokenRepository jwtTokenRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public void saveToken(String token, Long userId) {
        try {
            Date expirationDate = jwtTokenProvider.getExpirationDateFromToken(token);
            LocalDateTime expiresAt = new java.sql.Timestamp(expirationDate.getTime()).toLocalDateTime();

            JwtToken jwtToken = new JwtToken();
            jwtToken.setToken(token);
            jwtToken.setUserId(userId);
            jwtToken.setExpiresAt(expiresAt);

            jwtTokenRepository.save(jwtToken);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save JWT token", e);
        }
    }

    public boolean isTokenValid(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            return false;
        }
        return jwtTokenRepository.findByToken(token).isPresent();
    }

    public void revokeToken(String token) {
        jwtTokenRepository.deleteByToken(token);
    }

    public void revokeAllUserTokens(Long userId) {
        jwtTokenRepository.deleteByUserId(userId);
    }

    @Scheduled(fixedRate = 3600000)
    public void cleanupExpiredTokens() {
        jwtTokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }
}

