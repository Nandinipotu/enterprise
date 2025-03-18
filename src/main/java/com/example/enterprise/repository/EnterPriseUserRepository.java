package com.example.enterprise.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.enterprise.entity.EnterPriseUser;
import com.example.enterprise.entity.RefreshToken;

public interface EnterPriseUserRepository extends MongoRepository<EnterPriseUser, String>{

    Optional<EnterPriseUser> findByUserEmail(String email);

    Optional<EnterPriseUser> findByUserId(String userId);
    
}
