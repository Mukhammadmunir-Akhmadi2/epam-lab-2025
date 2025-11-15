package com.epam.application.services;

public interface TrainerAuthService {
    String toggleActive(String trainerUsername);
    void changePassword(String trainerUsername, String oldPassword, String newPassword);
}
