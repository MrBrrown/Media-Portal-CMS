package com.cms.repository;

import com.cms.model.document.JwtToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JwtTokenRepository extends MongoRepository<JwtToken, String> {
    Optional<JwtToken> findByToken(String token);
    List<JwtToken> findByUserId(Long userId);
    void deleteByToken(String token);
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
    void deleteByUserId(Long userId);
}

