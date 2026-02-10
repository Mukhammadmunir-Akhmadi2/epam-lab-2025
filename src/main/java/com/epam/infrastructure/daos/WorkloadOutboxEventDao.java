package com.epam.infrastructure.daos;

import com.epam.infrastructure.enums.OutboxEventType;
import com.epam.infrastructure.enums.OutboxStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "workload_outbox",
        indexes = {
                @Index(name="idx_outbox_status_next", columnList="status,next_attempt_at"),
                @Index(name="idx_outbox_tx", columnList="transaction_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@NamedQueries({
        @NamedQuery(
                name = "WorkloadOutboxEventDao.findBatchForRetry",
                query = """
            SELECT e FROM WorkloadOutboxEventDao e
            WHERE (e.status = 'NEW' OR e.status = 'RETRY')
              AND e.nextAttemptAt <= :now
            ORDER BY e.createdAt ASC
        """
        )
})
public class WorkloadOutboxEventDao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "woe_id")
    private UUID woeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type",nullable = false)
    private OutboxEventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private OutboxStatus status;

    @Column(name = "attempts", nullable = false)
    private Integer attempts;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "next_attempt_at", nullable = false)
    private LocalDateTime nextAttemptAt;

    @Column(name = "transaction_id", nullable = false, length = 64)
    private String transactionId;

    @Column(name = "aggregate_id", nullable = false, length = 64)
    private String aggregateId;

    @Lob
    @Column(name = "payload_json", nullable = false)
    private String payloadJson;

    @Column(name = "last_error", length = 1000)
    private String lastError;
}
