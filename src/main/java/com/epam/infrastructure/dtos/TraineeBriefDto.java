package com.epam.infrastructure.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Brief information about a trainee")
public class TraineeBriefDto {
    @Schema(description = "Username of the trainee", example = "john.doe")
    private String username;
    @Schema(description = "First name of the trainee", example = "John")
    private String firstName;
    @Schema(description = "Last name of the trainee", example = "Doe")
    private String lastName;
}
