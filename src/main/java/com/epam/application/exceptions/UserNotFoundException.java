package com.epam.application.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
    public UserNotFoundException(String userType, String id) {
        super(userType + " not found id=" + id);
    }
}
