package com.epam.infrastructure.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Trainee profile data transfer object")
public class TraineeDto {
    @NotBlank
    @Schema(description = "Trainee username", example = "john.doe")
    private String username;
    @NotBlank
    @Schema(description = "First name", example = "John")
    private String firstName;
    @NotBlank
    @Schema(description = "Last name", example = "Doe")
    private String lastName;
    @Schema(description = "Date of birth", example = "1999-10-10")
    private String dateOfBirth;
    @Schema(description = "Home address", example = "123 Main St")
    private String address;
    @NotNull
    @Schema(description = "Is trainee active?", example = "true")
    private boolean isActive;
}
