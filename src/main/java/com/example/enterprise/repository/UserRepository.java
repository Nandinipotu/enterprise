package com.example.enterprise.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.enterprise.entity.User;

public interface UserRepository extends MongoRepository<User, String>{

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByUserId(String userId);
    
}
