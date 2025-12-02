package com.epam.infrastructure.controllers;

import com.epam.infrastructure.dtos.TraineeTrainingDto;
import com.epam.infrastructure.dtos.TrainerTrainingDto;
import com.epam.infrastructure.dtos.TrainingDto;
import com.epam.infrastructure.enums.TrainingTypeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@RequestMapping("/trainings")
@Tag(name = "Training Management", description = "Endpoints for managing and retrieving trainings")
public interface TrainingController {

    @PostMapping("/trainee/{username}/trainer/{trainerUsername}/trainings")
    @Operation(
            summary = "Add new training",
            description = "Creates a training for a specific trainee with a specific trainer.",
            parameters = {
                    @Parameter(name = "username", description = "Trainee username", required = true),
                    @Parameter(name = "trainerUsername", description = "Trainer username", required = true)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Training details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = TrainingDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Training created successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access"),
                    @ApiResponse(responseCode = "400", description = "Invalid request"),
                    @ApiResponse(responseCode = "404", description = "Trainee or Trainer not found")
            }
    )
    ResponseEntity<Void> addTraining(
            @PathVariable String username,
            @PathVariable String trainerUsername,
            @Valid @RequestBody TrainingDto request
    );

    @GetMapping("/trainee/{username}")
    @Operation(
            summary = "Get trainee trainings",
            description = "Retrieves all trainings for a given trainee, optionally filtered by date, trainer, or type.",
            parameters = {
                    @Parameter(name = "username", description = "Trainee username", required = true),
                    @Parameter(name = "from", description = "Start date filter (optional)"),
                    @Parameter(name = "to", description = "End date filter (optional)"),
                    @Parameter(name = "trainerUsername", description = "Trainer name filter (optional)"),
                    @Parameter(name = "trainingType", description = "Training type filter (optional)")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Trainings retrieved"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access"),
                    @ApiResponse(responseCode = "404", description = "Trainee not found")
            }
    )
    ResponseEntity<List<TrainerTrainingDto>> getTraineeTrainings(
            @PathVariable String username,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            @RequestParam(required = false) String trainerUsername,
            @RequestParam(required = false) TrainingTypeEnum trainingType
    );


    @GetMapping("/trainer/{username}")
    @Operation(
            summary = "Get trainer trainings",
            description = "Retrieves all trainings conducted by a given trainer, optionally filtered by date or trainee.",
            parameters = {
                    @Parameter(name = "username", description = "Trainer username", required = true),
                    @Parameter(name = "from", description = "Start date filter (optional)"),
                    @Parameter(name = "to", description = "End date filter (optional)"),
                    @Parameter(name = "traineeUsername", description = "Trainee name filter (optional)")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Trainings retrieved"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access"),
                    @ApiResponse(responseCode = "404", description = "Trainer not found")
            }
    )
    ResponseEntity<List<TraineeTrainingDto>> getTrainerTrainings(
            @PathVariable String username,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            @RequestParam(required = false) String traineeUsername
    );
}
