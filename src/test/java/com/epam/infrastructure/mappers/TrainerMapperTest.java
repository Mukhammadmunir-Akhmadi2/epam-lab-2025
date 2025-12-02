package com.epam.infrastructure.mappers;

import com.epam.infrastructure.daos.TrainerDao;
import com.epam.infrastructure.dtos.AuthDto;
import com.epam.infrastructure.dtos.TrainerBriefDto;
import com.epam.infrastructure.dtos.TrainerDto;
import com.epam.infrastructure.dtos.TrainerRegistrationRequest;
import com.epam.model.Trainer;
import com.epam.model.TrainingType;
import com.epam.infrastructure.enums.TrainingTypeEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class TrainerMapperTest {

    @Autowired
    private TrainerMapper trainerMapper;

    @Test
    void testToBriefModel() {
        TrainerDao dao = new TrainerDao();
        dao.setUserId(UUID.randomUUID());
        dao.setFirstName("John");
        dao.setLastName("Doe");
        dao.setUsername("john_doe");

        Trainer trainer = trainerMapper.toBriefModel(dao);

        assertNotNull(trainer);
        assertEquals("John", trainer.getFirstName());
        assertEquals("Doe", trainer.getLastName());
        assertEquals("john_doe", trainer.getUsername());
    }

    @Test
    void testToModelFromDto() {
        TrainerDto dto = new TrainerDto();
        dto.setUsername("trainer123");
        dto.setFirstName("Jane");
        dto.setLastName("Smith");
        dto.setActive(true);

        Trainer trainer = trainerMapper.toModel(dto, new TrainingType(null, TrainingTypeEnum.CARDIO));

        assertNotNull(trainer);
        assertEquals("trainer123", trainer.getUsername());
        assertEquals("Jane", trainer.getFirstName());
        assertEquals("Smith", trainer.getLastName());
        assertTrue(trainer.isActive());
        assertEquals(TrainingTypeEnum.CARDIO, trainer.getSpecialization().getTrainingType());
    }

    @Test
    void testToModelFromRegistrationRequest() {
        TrainerRegistrationRequest request = new TrainerRegistrationRequest();
        request.setFirstName("Alice");
        request.setLastName("Brown");

        Trainer trainer = trainerMapper.toModel(request, new TrainingType(null, TrainingTypeEnum.STRENGTH));

        assertNotNull(trainer);
        assertEquals("Alice", trainer.getFirstName());
        assertEquals("Brown", trainer.getLastName());
        assertEquals(TrainingTypeEnum.STRENGTH, trainer.getSpecialization().getTrainingType());
    }

    @Test
    void testToBriefDtoAndAuthDto() {
        Trainer trainer = new Trainer();
        trainer.setUserId(UUID.randomUUID().toString());
        trainer.setUsername("bob_trainer");
        trainer.setFirstName("Bob");
        trainer.setLastName("Marley");
        trainer.setSpecialization(new TrainingType(null, TrainingTypeEnum.STRENGTH));
        trainer.setPassword("secret");

        TrainerBriefDto briefDto = trainerMapper.toBriefDto(trainer);
        AuthDto authDto = trainerMapper.toAuthDto(trainer);

        assertNotNull(briefDto);
        assertEquals("Bob", briefDto.getFirstName());
        assertEquals(TrainingTypeEnum.STRENGTH, briefDto.getSpecialization());

        assertNotNull(authDto);
        assertEquals("bob_trainer", authDto.getUsername());
        assertEquals("secret", authDto.getPassword());
    }

    @Test
    void testCollectionMappings() {
        TrainerDao dao1 = new TrainerDao();
        dao1.setUserId(UUID.randomUUID());
        TrainerDao dao2 = new TrainerDao();
        dao2.setUserId(UUID.randomUUID());

        List<Trainer> list = trainerMapper.toModelList(List.of(dao1, dao2));
        assertEquals(2, list.size());
    }

    @Test
    void testUpdateFields() {
        TrainerDao original = new TrainerDao();
        original.setUserId(UUID.randomUUID());
        original.setUsername("original_user");
        original.setFirstName("John");
        original.setLastName("Doe");

        TrainerDao update = new TrainerDao();
        update.setUserId(UUID.randomUUID()); // should be ignored
        update.setUsername("new_user"); // should be ignored
        update.setFirstName("Jane");
        update.setLastName("Smith");

        trainerMapper.updateFields(update, original);

        assertEquals("original_user", original.getUsername()); // unchanged
        assertEquals(update.getFirstName(), original.getFirstName());
        assertEquals(update.getLastName(), original.getLastName());
    }

    @Test
    void testNamedMappingsForSets() {
        Trainer trainer1 = new Trainer();
        trainer1.setUserId(UUID.randomUUID().toString());
        trainer1.setSpecialization(new TrainingType(null, TrainingTypeEnum.CARDIO));
        Trainer trainer2 = new Trainer();
        trainer2.setUserId(UUID.randomUUID().toString());
        trainer2.setSpecialization(new TrainingType(null, TrainingTypeEnum.STRENGTH));

        Set<TrainerDao> daoSet = trainerMapper.mapTrainerDaos(Set.of(trainer1, trainer2));
        assertEquals(2, daoSet.size());

        Set<Trainer> trainerSet = trainerMapper.mapTrainers(daoSet);
        assertEquals(2, trainerSet.size());
    }
}
