package com.epam.infrastructure.mappers;

import com.epam.infrastructure.daos.TrainingTypeDao;
import com.epam.infrastructure.dtos.TrainingTypeDto;
import com.epam.infrastructure.enums.TrainingTypeEnum;
import com.epam.model.TrainingType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class TrainingTypeMapperTest {

    @Autowired
    private TrainingTypeMapper trainingTypeMapper;

    @Test
    void testToModel() {
        TrainingTypeDao dao = new TrainingTypeDao();
        dao.setTrainingTypeId(UUID.randomUUID());
        dao.setTrainingType(TrainingTypeEnum.CARDIO);

        TrainingType model = trainingTypeMapper.toModel(dao);
        assertNotNull(model);
        assertEquals(TrainingTypeEnum.CARDIO, model.getTrainingType());
        assertNotNull(model.getTrainingTypeId());
    }

    @Test
    void testToDto() {
        TrainingType model = new TrainingType(UUID.randomUUID().toString(), TrainingTypeEnum.STRENGTH);
        TrainingTypeDto dto = trainingTypeMapper.toDto(model);

        assertNotNull(dto);
        assertEquals(TrainingTypeEnum.STRENGTH, dto.getTrainingType());
    }

    @Test
    void testToDao() {
        TrainingType model = new TrainingType(UUID.randomUUID().toString(), TrainingTypeEnum.CARDIO);
        TrainingTypeDao dao = trainingTypeMapper.toDao(model);

        assertNotNull(dao);
        assertEquals(TrainingTypeEnum.CARDIO, dao.getTrainingType());
    }

    @Test
    void testEnumMapping() {
        TrainingType type = new TrainingType();
        type.setTrainingType(TrainingTypeEnum.STRENGTH);

        assertEquals(TrainingTypeEnum.STRENGTH, trainingTypeMapper.trainingTypeToEnum(type));

        TrainingType mappedType = trainingTypeMapper.enumToTrainingType(TrainingTypeEnum.CARDIO);
        assertEquals(TrainingTypeEnum.CARDIO, mappedType.getTrainingType());
    }

    @Test
    void testCollectionMapping() {
        TrainingType type1 = new TrainingType(UUID.randomUUID().toString(), TrainingTypeEnum.CARDIO);
        TrainingType type2 = new TrainingType(UUID.randomUUID().toString(), TrainingTypeEnum.STRENGTH);

        List<TrainingTypeDto> dtos = trainingTypeMapper.toDtoList(List.of(type1, type2));
        assertEquals(2, dtos.size());

        List<TrainingType> models = trainingTypeMapper.toModelList(List.of(
                trainingTypeMapper.toDao(type1),
                trainingTypeMapper.toDao(type2)
        ));
        assertEquals(2, models.size());
    }
}
