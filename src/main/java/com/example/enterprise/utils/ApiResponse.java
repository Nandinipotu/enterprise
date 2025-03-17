package com.example.enterprise.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ApiResponse {
    
    private boolean status;
    private String message;
    private Object data;
    
    public ApiResponse(boolean status, String message) {
        this.status = status;
        this.message = message;
    }
    
}