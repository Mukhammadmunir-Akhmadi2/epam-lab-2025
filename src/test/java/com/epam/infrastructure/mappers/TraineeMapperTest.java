package com.epam.infrastructure.mappers;

import com.epam.infrastructure.daos.TraineeDao;
import com.epam.infrastructure.dtos.AuthDto;
import com.epam.infrastructure.dtos.TraineeBriefDto;
import com.epam.infrastructure.dtos.TraineeDto;
import com.epam.infrastructure.dtos.TraineeRegistrationRequest;
import com.epam.model.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TraineeMapperTest {

    private TraineeMapper traineeMapper;

    @BeforeEach
    void setUp() {
        traineeMapper = Mappers.getMapper(TraineeMapper.class);
    }

    @Test
    void testToBriefModel() {
        TraineeDao dao = new TraineeDao();
        dao.setUserId(UUID.randomUUID());
        dao.setFirstName("John");
        dao.setLastName("Doe");

        Trainee trainee = traineeMapper.toBriefModel(dao);

        assertNotNull(trainee);
        assertEquals(dao.getUserId().toString(), trainee.getUserId());
        assertEquals("John", trainee.getFirstName());
        assertEquals("Doe", trainee.getLastName());
    }

    @Test
    void testToModelFromDto() {
        TraineeDto dto = new TraineeDto();
        dto.setFirstName("Jane");
        dto.setLastName("Doe");
        dto.setDateOfBirth("2000-01-01");

        Trainee trainee = traineeMapper.toModel(dto);

        assertNotNull(trainee);
        assertEquals("Jane", trainee.getFirstName());
        assertEquals("Doe", trainee.getLastName());
        assertEquals(LocalDate.parse("2000-01-01"), trainee.getDateOfBirth());
        assertNull(trainee.getUserId());
        assertNull(trainee.getPassword());
    }

    @Test
    void testToModelFromRegistrationRequest() {
        TraineeRegistrationRequest request = new TraineeRegistrationRequest();
        request.setFirstName("Alice");
        request.setLastName("Smith");
        request.setDateOfBirth("1995-05-15");

        Trainee trainee = traineeMapper.toModel(request);

        assertNotNull(trainee);
        assertEquals("Alice", trainee.getFirstName());
        assertEquals("Smith", trainee.getLastName());
        assertEquals(LocalDate.parse("1995-05-15"), trainee.getDateOfBirth());
    }

    @Test
    void testToBriefDtoAndAuthDto() {
        Trainee trainee = new Trainee();
        trainee.setUserId(UUID.randomUUID().toString());
        trainee.setFirstName("Bob");
        trainee.setLastName("Brown");
        trainee.setUsername("Bob.Brown");

        TraineeBriefDto briefDto = traineeMapper.toBriefDto(trainee);
        AuthDto authDto = traineeMapper.toAuthDto(trainee);

        assertNotNull(briefDto);
        assertEquals("Bob", briefDto.getFirstName());
        assertNotNull(authDto);
        assertEquals("Bob.Brown", authDto.getUsername());
    }

    @Test
    void testCollectionMappings() {
        TraineeDao dao1 = new TraineeDao();
        dao1.setUserId(UUID.randomUUID());
        TraineeDao dao2 = new TraineeDao();
        dao2.setUserId(UUID.randomUUID());

        List<Trainee> list = traineeMapper.toModelList(List.of(dao1, dao2));
        assertEquals(2, list.size());
        assertEquals(dao1.getUserId().toString(), list.get(0).getUserId());
    }

    @Test
    void testUpdateFields() {
        TraineeDao original = new TraineeDao();
        original.setUserId(UUID.randomUUID());
        original.setUsername("original_user");
        original.setFirstName("John");
        original.setLastName("Doe");

        TraineeDao update = new TraineeDao();
        update.setUserId(UUID.randomUUID());
        update.setUsername("new_user");
        update.setFirstName("Jane");
        update.setLastName("Smith");

        traineeMapper.updateFields(update, original);

        assertEquals("Jane", original.getFirstName());
        assertEquals(update.getFirstName(), original.getFirstName());
        assertEquals(update.getLastName(), original.getLastName());
    }

    @Test
    void testNamedMappingsForSets() {
        Trainee trainee1 = new Trainee();
        trainee1.setUserId(UUID.randomUUID().toString());
        Trainee trainee2 = new Trainee();
        trainee2.setUserId(UUID.randomUUID().toString());

        Set<TraineeDao> daoSet = traineeMapper.mapTraineeDaos(Set.of(trainee1, trainee2));
        assertEquals(2, daoSet.size());

        Set<Trainee> traineeSet = traineeMapper.mapTrainees(daoSet);
        assertEquals(2, traineeSet.size());
    }
}
