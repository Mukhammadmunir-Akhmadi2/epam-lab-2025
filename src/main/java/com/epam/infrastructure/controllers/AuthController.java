package com.epam.infrastructure.controllers;

import com.epam.infrastructure.dtos.ChangePasswordRequest;
import com.epam.infrastructure.dtos.AuthDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/users")
@Tag(name = "User Authentication", description = "Endpoints for login, changing password, and account activation status")
public interface AuthController {
    @PostMapping("/login")
    @Operation(
            summary = "User login",
            description = "Authenticates a user using a username and password.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Login credentials",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AuthDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login successful"),
                    @ApiResponse(responseCode = "401", description = "Invalid password for user"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    ResponseEntity<Void> login(@Valid @RequestBody AuthDto loginRequest);

    @PutMapping("/{username}/password")
    @Operation(
            summary = "Change user password",
            description = "Updates the user's password using the old and new password.",
            parameters = {
                    @Parameter(
                            name = "username",
                            description = "The username of the account to change password for",
                            required = true)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Password change request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ChangePasswordRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password changed successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    ResponseEntity<Void> changePassword(@PathVariable String username, @Valid @RequestBody  ChangePasswordRequest request);


    @PatchMapping("/{username}/active")
    @Operation(
            summary = "Toggle user active status",
            description = "Activates or deactivates a user account.",
            parameters = {
                    @Parameter(
                            name = "username",
                            description = "The username of the account to toggle",
                            required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Account status updated"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )    ResponseEntity<Void> toggleActive(@PathVariable String username);
}
