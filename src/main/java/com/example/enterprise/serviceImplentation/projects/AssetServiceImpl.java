package com.example.enterprise.serviceImplentation.projects;

import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.enterprise.dto.request.LoginRequest;
import com.example.enterprise.service.project.AssetService;
import com.example.enterprise.utils.ApiResponse;

import jakarta.validation.ConstraintViolation;

public class AssetServiceImpl implements AssetService{

    @Override
    public ResponseEntity<ApiResponse> assetSignIn() {
        try {
        //    Document data = 
        //     Map<String, Object> userDataMap = Map.ofEntries(
        //             entry("id", userDetails.getId()),
        //             entry("domian", userDetails.getDomain()),
        //             entry("phoneNo", userDetails.getPhoneNo()),
        //             entry("plant", userDetails.getPlant()),
        //             entry("role", userDetails.getAuthorities()),
        //             entry("userId", userDetails.getUserId()),
        //             entry("username", userDetails.getFullName()),
        //             entry("email", userDetails.getEmail()),
        //             entry("profile", userDetails.getPictureWithPath()),
        //             entry("companyId", userDetails.getCompanyId()),
        //             entry("refreshToken", refreshToken.getToken()),
        //             entry("refreshTokenExpiryTime", formattedExpiryTime));

        //     String jwt = jwtUtils.generateJwtToken(userDetails.getEmail(), userDataMap);

        //     Date expirationTime = jwtUtils.getExpirationDateFromJwtToken(jwt);
        //     List<UserResponse> userResponses = new ArrayList<>();
        //     UserResponse userResponse = new UserResponse();
        //     userResponse.setExpirationTime(expirationTime);
        //     userResponse.setToken(jwt);
        //     userResponse.setRefreshToken(refreshToken.getToken());
        //     userResponse.setRefreshTokenExpiryTime(formattedExpiryTime);
        //     userResponses.add(userResponse);

            return ResponseEntity.ok().body(new ApiResponse(true, "User Login Successfully"));
        } catch (UsernameNotFoundException | BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse(false, "Credentials Mismatch"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Internal Server Error", e.getMessage()));
        }
    }
    
}
