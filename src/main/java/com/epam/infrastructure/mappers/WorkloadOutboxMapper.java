package com.epam.infrastructure.mappers;

import com.epam.infrastructure.daos.WorkloadOutboxEventDao;
import com.epam.infrastructure.outbox.entity.WorkloadOutboxEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {CommonMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface WorkloadOutboxMapper {

    @Mapping(source = "woeId", target = "woeId", qualifiedByName = "uuidToString")
    WorkloadOutboxEvent toModel(WorkloadOutboxEventDao dao);

    @Mapping(source = "woeId", target = "woeId", qualifiedByName = "stringToUuid")
    WorkloadOutboxEventDao toDao(WorkloadOutboxEvent model);

    List<WorkloadOutboxEvent> toModelList(List<WorkloadOutboxEventDao> daoList);
}
