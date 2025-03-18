package com.example.enterprise.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.enterprise.entity.RefreshToken;

public interface AssetRefreshTokenRepository extends MongoRepository<RefreshToken, String>{

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUsers(String userId);

    
}
