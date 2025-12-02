package com.epam.infrastructure.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request payload to change user password")
public class ChangePasswordRequest {
    @NotBlank
    @Schema(description = "Old password", example = "password123")
    private String oldPassword;
    @NotBlank
    @Size(min = 6, max = 18, message = "Password must be between 6 and 18 characters")
    @Schema(description = "New Password", example = "newpassword456")
    private String newPassword;
}