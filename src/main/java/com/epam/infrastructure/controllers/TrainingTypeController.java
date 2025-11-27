package com.epam.infrastructure.controllers;

import com.epam.infrastructure.dtos.TrainingTypeDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/training-types")
@Tag(name = "Training Types", description = "Retrieve available training types")
public interface TrainingTypeController {

    @GetMapping
    @Operation(
            summary = "Get all training types",
            description = "Returns a list of all available training types and their IDs.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of training types retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access")
            }
    )
    ResponseEntity<List<TrainingTypeDto>> getTrainingTypes();
}
