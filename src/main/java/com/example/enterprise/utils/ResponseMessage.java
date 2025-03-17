package com.example.enterprise.utils;

public enum ResponseMessage {
    // Success Messages
    USER_LOGGED_IN_SUCCESS("User logged in successfully"),
    USER_REGISTERED_SUCCESS("User registered successfully"),
    DATA_FETCHED_SUCCESS("Data fetched successfully"),
    DATA_SAVED_SUCCESS("Data saved successfully"),
    DATA_UPDATED_SUCCESS("Data updated successfully"),
    DATA_DELETED_SUCCESS("Data deleted successfully"),
    
    // Error Messages
    ERROR_OCCURRED("An error occurred"),
    INVALID_CREDENTIALS("Invalid username or password"),
    UNAUTHORIZED_ACCESS("Unauthorized access"),
    FORBIDDEN("Access denied"),
    USER_NOT_FOUND("User not found"),
    DATA_NOT_FOUND("Requested data not found"),
    VALIDATION_FAILED("Validation failed"),
    USER_ALREADY_EXISTS("User already exists"),
    USER_REGISTERED_SUCCESSFULLY("User registered successfully"), 
    USER_ALREADY_LOGGED_IN("User already logged In"),
    
    // Token Messages
    TOKEN_EXPIRED("Token has expired"),
    TOKEN_INVALID("Invalid token"),
    TOKEN_MISSING("Token is missing"),
    TOKEN_BLACKLISTED("Token black listed"), 
    USER_LOGGED_OUT_SUCCESSFULLY("Logged out successfully") ;

    private final String message;

    ResponseMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
