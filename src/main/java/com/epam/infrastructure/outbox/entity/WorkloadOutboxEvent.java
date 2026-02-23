package com.epam.infrastructure.outbox.entity;

import com.epam.infrastructure.enums.OutboxEventType;
import com.epam.infrastructure.enums.OutboxStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkloadOutboxEvent {
    private String woeId;
    @NotNull
    private OutboxEventType eventType;
    @NotNull
    private OutboxStatus status;
    @NotNull
    @Min(0)
    private Integer attempts;
    @NotNull
    @PastOrPresent
    private LocalDateTime createdAt;
    @NotNull
    private LocalDateTime nextAttemptAt;
    @NotBlank
    private String transactionId;
    @NotBlank
    private String aggregateId;
    @NotBlank
    private Map<String, Object> payload;
    private String lastError;
}
