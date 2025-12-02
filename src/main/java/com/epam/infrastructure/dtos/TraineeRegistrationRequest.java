package com.epam.infrastructure.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request payload for registering a new trainee")
public class TraineeRegistrationRequest {
    @NotBlank
    @Schema(description = "First name of the trainee", example = "John")
    private String firstName;
    @NotBlank
    @Schema(description = "Last name of the trainee", example = "Doe")
    private String lastName;
    @Schema(description = "Home address", example = "123 Main St")
    private String address;
    @Schema(description = "Date of birth", example = "1999-10-10")
    private String dateOfBirth;
}