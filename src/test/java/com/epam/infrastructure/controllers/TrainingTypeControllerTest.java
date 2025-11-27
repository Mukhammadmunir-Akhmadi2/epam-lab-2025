package com.epam.infrastructure.controllers;

import com.epam.application.exceptions.UnauthorizedAccess;
import com.epam.application.services.TrainingTypeService;
import com.epam.infrastructure.controllers.Impl.TrainingTypeControllerImpl;
import com.epam.infrastructure.dtos.TrainingTypeDto;
import com.epam.infrastructure.enums.TrainingTypeEnum;
import com.epam.infrastructure.mappers.TrainingTypeMapper;
import com.epam.model.TrainingType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrainingTypeControllerImpl.class)
class TrainingTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private TrainingTypeService trainingTypeService;

    @MockitoBean
    private TrainingTypeMapper trainingTypeMapper;


    @Test
    void getTrainingTypes_ShouldReturnOkAndList() throws Exception {
        TrainingType type1 = new TrainingType();
        TrainingType type2 = new TrainingType();

        TrainingTypeDto dto1 = new TrainingTypeDto();
        dto1.setTrainingType(TrainingTypeEnum.YOGA);
        dto1.setTrainingTypeId(UUID.randomUUID().toString());
        TrainingTypeDto dto2 = new TrainingTypeDto();
        dto2.setTrainingType(TrainingTypeEnum.CARDIO);
        dto2.setTrainingTypeId(UUID.randomUUID().toString());

        when(trainingTypeService.getAllTrainingTypes()).thenReturn(List.of(type1, type2));
        when(trainingTypeMapper.toDtoList(List.of(type1, type2))).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/training-types")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].trainingType").value("YOGA"))
                .andExpect(jsonPath("$[1].trainingType").value("CARDIO"));
    }


    @Test
    void getTrainingTypes_Unauthorized_ShouldReturnUnauthorized() throws Exception {
        when(trainingTypeService.getAllTrainingTypes())
                .thenThrow(new UnauthorizedAccess("You must log in"));

        mockMvc.perform(get("/training-types"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("Unauthorized Access"))
                .andExpect(jsonPath("$.detail").value("You must log in"));
    }
}
