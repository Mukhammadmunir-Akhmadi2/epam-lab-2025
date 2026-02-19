package com.epam.infrastructure.outbox.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class OutboxSerializer {
    private final ObjectMapper objectMapper;

    public String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize outbox payload", e);
        }
    }

    public Map<String, Object> toMap(Object obj) {
        try {
            return objectMapper.convertValue(
                    obj,
                    new TypeReference<Map<String, Object>>() {}
            );
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Failed to convert outbox payload to map", e);
        }
    }

    public <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to deserialize outbox payload", e);
        }
    }
    public <T> T fromMap(Map<String, Object> payload, Class<T> clazz) {
        try {
            return objectMapper.convertValue(payload, clazz);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Failed to convert outbox payload map", e);
        }
    }
}