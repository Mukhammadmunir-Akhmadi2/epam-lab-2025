package com.epam.infrastructure.integration;

import com.epam.application.port.WorkloadServiceClient;
import com.epam.infrastructure.dtos.TrainerWorkloadRequestDto;
import com.epam.infrastructure.logging.TransactionIdFilter;
import com.epam.infrastructure.outbox.WorkloadOutboxPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Log4j2
@RequiredArgsConstructor
public class WorkloadServiceClientImpl implements WorkloadServiceClient {

    @Value("${app.kafka.topics.workload-events}")
    private String topic;

    private final WorkloadOutboxPort outboxPort;
    private final KafkaHeadersProvider headersProvider;
    private final KafkaTemplate<String, TrainerWorkloadRequestDto> kafkaTemplate;

    @Override
    @CircuitBreaker(name = "workload", fallbackMethod = "sendFallback")
    public void send(TrainerWorkloadRequestDto req) {
        String key = req.getTrainerUsername();
        String txId = headersProvider.currentTransactionId();
        String auth = headersProvider.currentAuthorizationValue();

        Message<TrainerWorkloadRequestDto> message = MessageBuilder
                .withPayload(req)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(KafkaHeaders.KEY, key)
                .setHeader(TransactionIdFilter.TRANSACTION_ID_HEADER, txId)
                .setHeader(KafkaHeadersProvider.AUTH_HEADER, auth)
                .build();

        try {
            var res = kafkaTemplate.send(message).get(2, TimeUnit.SECONDS);

            log.info("Kafka publish OK. topic={} partition={} offset={}",
                    res.getRecordMetadata().topic(),
                    res.getRecordMetadata().partition(),
                    res.getRecordMetadata().offset());

        } catch (Exception ex) {
            Throwable cause = (ex.getCause() != null) ? ex.getCause() : ex;
            enqueueToOutbox(req, cause);
        }
    }

    @SuppressWarnings("unused")
    private void sendFallback(TrainerWorkloadRequestDto req, Throwable ex) {
        enqueueToOutbox(req, ex);
    }

    private void enqueueToOutbox(TrainerWorkloadRequestDto req, Throwable cause) {
        log.error("Kafka publish failed -> outbox. trainer={} date={} cause={}",
                req.getTrainerUsername(), req.getTrainingDate(), cause.toString());

        String aggregateId = req.getTrainerUsername() + ":" + req.getTrainingDate();
        outboxPort.enqueue(req, aggregateId, cause.toString());
    }
}