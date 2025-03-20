package com.example.enterprise.serviceImplentation.projects;


import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.example.enterprise.dto.ProjectsDTO;
import com.example.enterprise.service.project.ProjectService;
import com.example.enterprise.utils.AuthUserDetails;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class ProjectServiceImplention implements ProjectService {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<ProjectsDTO> getProjectList() {
        String userId = AuthUserDetails.getUserId();
        Criteria criteria = Criteria.where("userId").is(userId);
        LookupOperation projectLookup = Aggregation.lookup("projects", "projectId", "projectId", "ProjectDetails");
        
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                projectLookup,
                Aggregation.unwind("ProjectDetails", true),
                Aggregation.project("ProjectDetails.projectId", "ProjectDetails.projectName"));

        return mongoTemplate.aggregate(aggregation,
                "enterprise_user_projects", ProjectsDTO.class).getMappedResults();
    }
    
}
