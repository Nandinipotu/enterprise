package com.example.enterprise.service.company;

import org.springframework.http.ResponseEntity;

import com.example.enterprise.utils.ApiResponse;

public interface CompanyService {
    
    ResponseEntity<ApiResponse> companyList();

}
