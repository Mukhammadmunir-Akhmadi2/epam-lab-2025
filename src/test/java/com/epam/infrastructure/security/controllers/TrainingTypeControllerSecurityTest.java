package com.epam.infrastructure.security.controllers;

import com.epam.application.services.TrainingTypeService;
import com.epam.infrastructure.dtos.TrainingTypeDto;
import com.epam.infrastructure.mappers.TrainingTypeMapper;
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

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TrainingTypeControllerSecurityTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TrainingTypeService trainingTypeService;

    @MockitoBean
    private TrainingTypeMapper trainingTypeMapper;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "user")
    void getTrainingTypes_WithAuth_ShouldReturnOk() throws Exception {
        List<TrainingType> types = List.of(new TrainingType());
        List<TrainingTypeDto> dtos = List.of(new TrainingTypeDto());

        when(trainingTypeService.getAllTrainingTypes()).thenReturn(types);
        when(trainingTypeMapper.toDtoList(types)).thenReturn(dtos);

        mockMvc.perform(get("/training-types"))
                .andExpect(status().isOk());
    }

    @Test
    void getTrainingTypes_WithoutAuth_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/training-types"))
                .andExpect(status().isUnauthorized());
    }
}
