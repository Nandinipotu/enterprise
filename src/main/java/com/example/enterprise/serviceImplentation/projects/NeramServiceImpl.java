package com.example.enterprise.serviceImplentation.projects;

import java.nio.file.Files;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.enterprise.dto.response.AssetResponse;
import com.example.enterprise.entity.EnterPriseUser;
import com.example.enterprise.jwt.JwtUtils;
import com.example.enterprise.repository.EnterPriseUserRepository;
import com.example.enterprise.service.project.NeramService;
import com.example.enterprise.utils.ApiResponse;
import com.example.enterprise.utils.AuthUserDetails;
import com.example.enterprise.utils.ProjectAccessToken;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NeramServiceImpl implements NeramService {

    private final EnterPriseUserRepository enterPriseUserRepository;
    private final AuthUserDetails authUserDetails;
    private static final String SECRET = "======================NeramToolApplication========================";
    private final MongoTemplate mongoTemplate;
    private final ProjectAccessToken projectAccessToken;
    public static final String TOKEN_TYPE_CLAIM = "tokenType";
    public static final String ACCESS_TOKEN_TYPE = "access";

    @Override
    public Map<String, Object> createNeramToken(String projectId, HttpServletRequest request) {
        // try{
            Map<String, Object> response = new HashMap<>();
        String userId = AuthUserDetails.getUserId();
        Optional<Document> userDetailsOpt = fetchUserDetails(userId, projectId);
        if (userDetailsOpt.isEmpty()) {
            response.put("error", "User details not found");
            return response;
        }

        Map<String, Object> data = getClaims(userDetailsOpt.get());
        String userName = data.get("employee_id").toString();
        String jwt = projectAccessToken.generateTokenFromUsernameintoClaims(userName, data, SECRET);
        Criteria criteria = Criteria.where("userId").is(userId).and("projectId").is(projectId);
        LookupOperation projectLookup = Aggregation.lookup("projects", "projectId", "projectId", "projectDetails");

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                projectLookup,
                Aggregation.unwind("projectDetails", true),
                Aggregation.project("projectDetails.projectURL").andExclude("_id"));
        Map<String, String> result = mongoTemplate.aggregate(aggregation, "enterprise_user_projects", Map.class)
                .getUniqueMappedResult();
        response.put("token", jwt);
        response.put("url", result.get("projectURL"));
        return response;

    }

    public Map<String, Object> getClaims(Document userDetails) {
        Map<String, Object> responseData = new LinkedHashMap<>();

        List<Document> requiredFields = (List<Document>) userDetails.get("requiredFields");

        if (requiredFields != null && !requiredFields.isEmpty()) {
            Document requiredField = requiredFields.get(0);

            responseData.put("id", requiredField.getInteger("id"));
            responseData.put("role", requiredField.getString("role"));
            responseData.put("finalApprover", requiredField.getString("finalApprover"));
            responseData.put("superviser", requiredField.getString("superviser"));
            responseData.put("employee_id", requiredField.getString("employee_id"));
            responseData.put("name", requiredField.getString("name"));
            responseData.put("profile_pic", requiredField.getString("profile_pic"));
            responseData.put("roleIntake", requiredField.getString("roleIntake"));
            responseData.put("designation", requiredField.getString("designation"));
            responseData.put("branch", requiredField.getString("branch"));
            responseData.put("email", requiredField.getString("email"));
            responseData.put("jod", requiredField.getString("jod"));
            responseData.put(TOKEN_TYPE_CLAIM, ACCESS_TOKEN_TYPE);

        }

        return responseData;
    }

    private Optional<Document> fetchUserDetails(String userId, String projectId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId).and("projectId").is(projectId));
        return Optional.ofNullable(mongoTemplate.findOne(query, Document.class, "enterprise_user_projects"));
    }
}
