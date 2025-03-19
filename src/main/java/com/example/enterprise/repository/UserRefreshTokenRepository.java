package com.example.enterprise.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.enterprise.entity.RefreshToken;
import com.example.enterprise.entity.User;
import com.example.enterprise.entity.UserRefreshToken;

public interface UserRefreshTokenRepository extends MongoRepository<UserRefreshToken, String>{

    Optional<UserRefreshToken> findByUsers(String userId);

    Optional<RefreshToken> findByToken(String token);

    String deleteByUsers(User user);
    
}
