package com.epam.infrastructure.integration;

import com.epam.infrastructure.outbox.WorkloadOutboxPort;
import com.epam.infrastructure.dtos.TrainerWorkloadRequestDto;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class WorkloadServiceClientImplTest {

    @Test
    void send_shouldDelegateToClient() {
        WorkloadClient client = mock(WorkloadClient.class);
        WorkloadOutboxPort outbox = mock(WorkloadOutboxPort.class);

        WorkloadServiceClientImpl svc = new WorkloadServiceClientImpl(client, outbox);

        TrainerWorkloadRequestDto req = new TrainerWorkloadRequestDto();
        req.setTrainerUsername("john");
        req.setTrainingDate(LocalDate.of(2026, 2, 9));
        req.setTrainingDuration(45);

        svc.send(req);

        verify(client, times(1)).send(req);
        verifyNoInteractions(outbox);
    }

    @Test
    void fallback_shouldEnqueueToOutbox_withAggregateIdAndError() throws Exception {
        WorkloadClient client = mock(WorkloadClient.class);
        WorkloadOutboxPort outbox = mock(WorkloadOutboxPort.class);

        WorkloadServiceClientImpl svc = new WorkloadServiceClientImpl(client, outbox);

        TrainerWorkloadRequestDto req = new TrainerWorkloadRequestDto();
        req.setTrainerUsername("john");
        req.setTrainingDate(LocalDate.of(2026, 2, 9));
        req.setTrainingDuration(45);

        RuntimeException ex = new RuntimeException("service down");

        // call private fallback(req, Throwable)
        Method m = WorkloadServiceClientImpl.class.getDeclaredMethod("fallback", TrainerWorkloadRequestDto.class, Throwable.class);
        m.setAccessible(true);

        assertDoesNotThrow(() -> m.invoke(svc, req, ex));

        String expectedAggregateId = "john:2026-02-09";
        verify(outbox, times(1)).enqueue(eq(req), eq(expectedAggregateId), contains("service down"));
        verifyNoInteractions(client);
    }
}
