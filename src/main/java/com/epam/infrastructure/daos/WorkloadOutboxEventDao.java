package com.epam.infrastructure.daos;

import com.epam.infrastructure.enums.OutboxEventType;
import com.epam.infrastructure.enums.OutboxStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
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

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload_json")
    private Map<String, Object> payload;

    @Column(name = "last_error", length = 1000)
    private String lastError;
}
