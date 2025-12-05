package com.epam.infrastructure.controllers;

import com.epam.application.exceptions.ResourceNotFoundException;
import com.epam.application.exceptions.UnauthorizedAccess;
import com.epam.application.provider.AuthProviderService;
import com.epam.application.services.TrainerService;
import com.epam.application.services.TrainingTypeService;
import com.epam.infrastructure.controllers.Impl.TrainerControllerImpl;
import com.epam.infrastructure.dtos.*;
import com.epam.infrastructure.enums.TrainingTypeEnum;
import com.epam.infrastructure.mappers.TrainerFullMapper;
import com.epam.infrastructure.mappers.TrainerMapper;
import com.epam.model.Trainer;
import com.epam.model.TrainingType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrainerControllerImpl.class)
class TrainerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private TrainerService trainerService;

    @MockitoBean
    private TrainingTypeService trainingTypeService;

    @MockitoBean
    private AuthProviderService authProvider;

    @MockitoBean
    private TrainerMapper trainerMapper;

    @MockitoBean
    private TrainerFullMapper trainerFullMapper;

    @Test
    void register_ShouldReturnCreated() throws Exception {
        TrainerRegistrationRequest request = new TrainerRegistrationRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setSpecialization(TrainingTypeEnum.YOGA);

        TrainingType specialization = new TrainingType();
        Trainer trainer = new Trainer();

        AuthDto authDto = new AuthDto();
        authDto.setUsername("john.doe");
        authDto.setPassword("pass123");

        when(trainingTypeService.getTrainingType(any(TrainingTypeEnum.class)))
                .thenReturn(specialization);

        when(trainerMapper.toModel(any(TrainerRegistrationRequest.class), any(TrainingType.class)))
                .thenReturn(trainer);

        when(trainerService.createTrainer(any())).thenReturn(trainer);

        when(trainerMapper.toAuthDto(any())).thenReturn(authDto);

        mockMvc.perform(post("/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("john.doe"))
                .andExpect(jsonPath("$.password").value("pass123"));
    }

    @Test
    void updateProfile_ShouldReturnOk() throws Exception {
        TrainerDto dto = new TrainerDto();
        dto.setUsername("john.doe");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setActive(true);
        dto.setSpecialization(TrainingTypeEnum.YOGA);

        TrainingType specialization = new TrainingType();
        Trainer trainer = new Trainer();
        TrainerResponseDto responseDto = new TrainerResponseDto();

        doNothing().when(authProvider).ensureAuthenticated(dto.getUsername());

        when(trainingTypeService.getTrainingType(any(TrainingTypeEnum.class)))
                .thenReturn(specialization);

        when(trainerMapper.toModel(any(TrainerDto.class), any(TrainingType.class)))
                .thenReturn(trainer);

        when(trainerService.updateTrainer(any())).thenReturn(trainer);

        when(trainerFullMapper.toTrainerResponseDto(any()))
                .thenReturn(responseDto);

        mockMvc.perform(put("/trainers/" + dto.getUsername())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void getProfile_ShouldReturnOk() throws Exception {
        String username = "john.doe";

        Trainer trainer = new Trainer();
        TrainerResponseDto responseDto = new TrainerResponseDto();

        doNothing().when(authProvider).ensureAuthenticated(username);

        when(trainerService.getTrainerByUserName(username)).thenReturn(trainer);
        when(trainerFullMapper.toTrainerResponseDto(any()))
                .thenReturn(responseDto);

        mockMvc.perform(get("/trainers/" + username))
                .andExpect(status().isOk());
    }

    @Test
    void getProfile_Unauthorized_ShouldReturnUnauthorized() throws Exception {
        String username = "john.doe";

        doThrow(new UnauthorizedAccess("Not allowed"))
                .when(authProvider).ensureAuthenticated(username);

        mockMvc.perform(get("/trainers/" + username))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("Unauthorized Access"))
                .andExpect(jsonPath("$.detail").value("Not allowed"));
    }

    @Test
    void getProfile_NotFound_ShouldReturnNotFound() throws Exception {
        String username = "unknown";

        doNothing().when(authProvider).ensureAuthenticated(username);

        when(trainerService.getTrainerByUserName(username))
                .thenThrow(new ResourceNotFoundException("Trainer not found"));

        mockMvc.perform(get("/trainers/" + username))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value("Trainer not found"));
    }

    @Test
    void updateProfile_ValidationFailure_ShouldReturnBadRequest() throws Exception {
        TrainerDto dto = new TrainerDto(); // missing required fields → triggers @Valid

        mockMvc.perform(put("/trainers/" + dto.getUsername())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_ValidationFailure_ShouldReturnBadRequest() throws Exception {
        TrainerRegistrationRequest request = new TrainerRegistrationRequest();

        mockMvc.perform(post("/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
