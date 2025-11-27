package com.epam.infrastructure.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Data Transfer Object for Authentication")
public class AuthDto {
    @NotBlank
    @Schema(description = "Username", example = "john_doe")
    private String username;
    @NotBlank
    @Schema(description = "Password", example = "password123")
    private String password;
}
