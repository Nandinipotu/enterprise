package com.example.enterprise.utils;


import com.example.enterprise.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiResponseUtil {
    private ApiResponseUtil() {
    }
    
    public static ResponseEntity<ApiResponse> success(String message, Object data) {
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), message, data));
    }

    public static  ResponseEntity<ApiResponse> created(Object data, String message) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(HttpStatus.CREATED.value(), message, data));
    }

    public static  ResponseEntity<ApiResponse> notFound(String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(HttpStatus.NOT_FOUND.value(), message, null));
    }

    public static  ResponseEntity<ApiResponse> badRequest(String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(HttpStatus.BAD_REQUEST.value(), message, null));
    }

    public static  ResponseEntity<ApiResponse> internalServerError(String message) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), message, null));
    }
}
