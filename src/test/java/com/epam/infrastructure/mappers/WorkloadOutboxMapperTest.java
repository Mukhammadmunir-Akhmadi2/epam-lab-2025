package com.epam.infrastructure.mappers;

import com.epam.infrastructure.daos.WorkloadOutboxEventDao;
import com.epam.infrastructure.enums.OutboxEventType;
import com.epam.infrastructure.enums.OutboxStatus;
import com.epam.infrastructure.outbox.entity.WorkloadOutboxEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class WorkloadOutboxMapperTest {

    @Autowired
    private WorkloadOutboxMapper mapper;

    @Test
    void toModel_shouldMapAllFields_andConvertUuidToString() {
        UUID id = UUID.randomUUID();

        WorkloadOutboxEventDao dao = new WorkloadOutboxEventDao();
        dao.setWoeId(id);
        dao.setEventType(OutboxEventType.TRAINING_WORKLOAD);
        dao.setStatus(OutboxStatus.NEW);
        dao.setAttempts(0);
        dao.setCreatedAt(LocalDateTime.of(2026, 2, 9, 12, 0));
        dao.setNextAttemptAt(LocalDateTime.of(2026, 2, 9, 12, 0).plusSeconds(5));
        dao.setTransactionId("tx-1");
        dao.setAggregateId("john:2026-02-09");
        dao.setPayloadJson("{\"a\":1}");
        dao.setLastError("err");

        WorkloadOutboxEvent model = mapper.toModel(dao);

        assertNotNull(model);
        assertEquals(id.toString(), model.getWoeId());
        assertEquals(OutboxEventType.TRAINING_WORKLOAD, model.getEventType());
        assertEquals(OutboxStatus.NEW, model.getStatus());
        assertEquals(0, model.getAttempts());
        assertEquals(dao.getCreatedAt(), model.getCreatedAt());
        assertEquals(dao.getNextAttemptAt(), model.getNextAttemptAt());
        assertEquals("tx-1", model.getTransactionId());
        assertEquals("john:2026-02-09", model.getAggregateId());
        assertEquals("{\"a\":1}", model.getPayloadJson());
        assertEquals("err", model.getLastError());
    }

    @Test
    void toDao_shouldMapAllFields_andConvertStringToUuid() {
        UUID id = UUID.randomUUID();

        WorkloadOutboxEvent model = new WorkloadOutboxEvent();
        model.setWoeId(id.toString());
        model.setEventType(OutboxEventType.TRAINING_WORKLOAD);
        model.setStatus(OutboxStatus.RETRY);
        model.setAttempts(3);
        model.setCreatedAt(LocalDateTime.of(2026, 2, 9, 10, 0));
        model.setNextAttemptAt(LocalDateTime.of(2026, 2, 9, 10, 0).plusSeconds(10));
        model.setTransactionId("tx-9");
        model.setAggregateId("t:2026-02-09");
        model.setPayloadJson("{\"x\":9}");
        model.setLastError(null);

        WorkloadOutboxEventDao dao = mapper.toDao(model);

        assertNotNull(dao);
        assertEquals(id, dao.getWoeId());
        assertEquals(OutboxEventType.TRAINING_WORKLOAD, dao.getEventType());
        assertEquals(OutboxStatus.RETRY, dao.getStatus());
        assertEquals(3, dao.getAttempts());
        assertEquals(model.getCreatedAt(), dao.getCreatedAt());
        assertEquals(model.getNextAttemptAt(), dao.getNextAttemptAt());
        assertEquals("tx-9", dao.getTransactionId());
        assertEquals("t:2026-02-09", dao.getAggregateId());
        assertEquals("{\"x\":9}", dao.getPayloadJson());
        assertNull(dao.getLastError());
    }

    @Test
    void toModelList_shouldMapListAndConvertIds() {
        WorkloadOutboxEventDao d1 = new WorkloadOutboxEventDao();
        d1.setWoeId(UUID.randomUUID());
        d1.setEventType(OutboxEventType.TRAINING_WORKLOAD);
        d1.setStatus(OutboxStatus.NEW);
        d1.setAttempts(0);
        d1.setCreatedAt(LocalDateTime.now());
        d1.setNextAttemptAt(LocalDateTime.now());
        d1.setTransactionId("tx1");
        d1.setAggregateId("a1");
        d1.setPayloadJson("{}");

        WorkloadOutboxEventDao d2 = new WorkloadOutboxEventDao();
        d2.setWoeId(UUID.randomUUID());
        d2.setEventType(OutboxEventType.TRAINING_WORKLOAD);
        d2.setStatus(OutboxStatus.RETRY);
        d2.setAttempts(1);
        d2.setCreatedAt(LocalDateTime.now());
        d2.setNextAttemptAt(LocalDateTime.now());
        d2.setTransactionId("tx2");
        d2.setAggregateId("a2");
        d2.setPayloadJson("{}");

        List<WorkloadOutboxEvent> list = mapper.toModelList(List.of(d1, d2));

        assertNotNull(list);
        assertEquals(2, list.size());
        assertEquals(d1.getWoeId().toString(), list.get(0).getWoeId());
        assertEquals(d2.getWoeId().toString(), list.get(1).getWoeId());
    }

    @Test
    void toModel_shouldSetNullId_whenDaoIdNull() {
        WorkloadOutboxEventDao dao = new WorkloadOutboxEventDao();
        dao.setWoeId(null);
        dao.setEventType(OutboxEventType.TRAINING_WORKLOAD);
        dao.setStatus(OutboxStatus.NEW);
        dao.setAttempts(0);
        dao.setCreatedAt(LocalDateTime.now());
        dao.setNextAttemptAt(LocalDateTime.now());
        dao.setTransactionId("tx");
        dao.setAggregateId("agg");
        dao.setPayloadJson("{}");

        WorkloadOutboxEvent model = mapper.toModel(dao);

        assertNotNull(model);
        assertNull(model.getWoeId());
    }
}
