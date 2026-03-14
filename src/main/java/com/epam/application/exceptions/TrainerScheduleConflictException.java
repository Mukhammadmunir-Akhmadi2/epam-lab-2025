package com.epam.application.exceptions;

public class TrainerScheduleConflictException extends RuntimeException {
    public TrainerScheduleConflictException(String message) {
        super(message);
    }
}
