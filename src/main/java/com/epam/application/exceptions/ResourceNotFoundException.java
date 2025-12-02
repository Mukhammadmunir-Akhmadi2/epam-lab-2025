package com.epam.application.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
    public ResourceNotFoundException(String userType, String id) {
        super(userType + " not found id=" + id);
    }
}
