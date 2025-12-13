package com.epam.infrastructure.security.controllers;

import com.epam.application.provider.AuthProviderService;
import com.epam.application.services.*;
import com.epam.infrastructure.dtos.*;
import com.epam.infrastructure.enums.TrainingTypeEnum;
import com.epam.infrastructure.mappers.*;
import com.epam.model.Role;
import com.epam.model.Trainer;
import com.epam.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.util.HashSet;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TrainerControllerSecurityTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TrainerService trainerService;
    @MockitoBean
    private TrainingTypeService trainingTypeService;
    @MockitoBean
    private AuthProviderService authProvider;
    @MockitoBean
    private RoleService roleService;
    @MockitoBean
    private TrainerMapper trainerMapper;
    @MockitoBean
    private TrainerFullMapper trainerFullMapper;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void register_WithoutAuth_ShouldReturnCreated() throws Exception {
        TrainerRegistrationRequest request = new TrainerRegistrationRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setSpecialization(TrainingTypeEnum.YOGA);

        Trainer trainer = new Trainer();
        trainer.setRoles(new HashSet<>());

        when(roleService.getRole(any()))
                .thenReturn(mock(Role.class));
        when(trainingTypeService.getTrainingType(any()))
                .thenReturn(mock(TrainingType.class));
        when(trainerMapper.toModel(
                any(TrainerRegistrationRequest.class),
                any(TrainingType.class)
        )).thenReturn(trainer);
        when(trainerService.createTrainer(any()))
                .thenReturn(trainer);
        when(trainerMapper.toAuthDto(any()))
                .thenReturn(new AuthDto());

        mockMvc.perform(post("/trainers")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void updateProfile_WithoutAuth_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(put("/trainers/user")
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user", authorities = "TRAINER")
    void updateProfile_WithTrainer_ShouldReturnOk() throws Exception {
        TrainerDto dto = new TrainerDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setUsername("user");
        dto.setSpecialization(TrainingTypeEnum.YOGA);
        dto.setActive(true);

        doNothing().when(authProvider).validateCurrentUser("user");
        when(trainingTypeService.getTrainingType(any()))
                .thenReturn(mock(TrainingType.class));
        when(trainerMapper.toModel(any(TrainerDto.class), any()))
                .thenReturn(mock(Trainer.class));
        when(trainerService.updateTrainer(any()))
                .thenReturn(mock(Trainer.class));
        when(trainerFullMapper.toTrainerResponseDto(any()))
                .thenReturn(new TrainerResponseDto());

        mockMvc.perform(put("/trainers/user")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "trainee", authorities = "TRAINEE")
    void updateProfile_WithTrainee_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(put("/trainers/user")
                        .contentType("application/json"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getProfile_WithoutAuth_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/trainers/user"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user", authorities = "TRAINER")
    void getProfile_WithTrainer_ShouldReturnOk() throws Exception {
        doNothing().when(authProvider).validateCurrentUser("user");
        when(trainerService.getTrainerByUserName("user"))
                .thenReturn(mock(Trainer.class));
        when(trainerFullMapper.toTrainerResponseDto(any()))
                .thenReturn(new TrainerResponseDto());

        mockMvc.perform(get("/trainers/user"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "trainee", authorities = "TRAINEE")
    void getProfile_WithTrainee_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/trainers/user"))
                .andExpect(status().isForbidden());
    }
}
