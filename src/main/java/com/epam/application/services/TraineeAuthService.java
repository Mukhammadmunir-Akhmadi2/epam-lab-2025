package com.epam.application.services;

public interface TraineeAuthService {
    String toggleActive(String traineeUsername);
    void changePassword(String traineeUsername, String oldPassword, String newPassword);
}
