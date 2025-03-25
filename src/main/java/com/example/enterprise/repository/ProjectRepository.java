package com.example.enterprise.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.enterprise.entity.Project;

public interface ProjectRepository extends MongoRepository<Project, String>{

    Optional<Project> findByProjectId(String projectId);
    
}
