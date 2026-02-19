package com.epam.infrastructure.outbox;

import com.epam.infrastructure.outbox.util.OutboxSerializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OutboxSerializerTest {

    @Test
    void toJson_shouldReturnJson() throws Exception {
        ObjectMapper om = mock(ObjectMapper.class);
        OutboxSerializer serializer = new OutboxSerializer(om);

        Object obj = new Object();
        when(om.writeValueAsString(obj)).thenReturn("{\"ok\":true}");

        String json = serializer.toJson(obj);

        assertEquals("{\"ok\":true}", json);
        verify(om).writeValueAsString(obj);
    }

    @Test
    void toJson_shouldThrowIllegalState_whenJacksonFails() throws Exception {
        ObjectMapper om = mock(ObjectMapper.class);
        OutboxSerializer serializer = new OutboxSerializer(om);

        Object obj = new Object();
        when(om.writeValueAsString(obj)).thenThrow(new JsonProcessingException("boom") {});

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> serializer.toJson(obj));

        assertEquals("Failed to serialize outbox payload", ex.getMessage());
        assertNotNull(ex.getCause());
        verify(om).writeValueAsString(obj);
    }

    @Test
    void toMap_shouldConvertObjectToMap() {
        ObjectMapper om = mock(ObjectMapper.class);
        OutboxSerializer serializer = new OutboxSerializer(om);

        Dummy obj = new Dummy(9, "x");
        Map<String, Object> expected = Map.of("a", 9, "b", "x");

        // TypeReference instance differs each call -> match with any(TypeReference.class)
        when(om.convertValue(eq(obj), any(TypeReference.class))).thenReturn(expected);

        Map<String, Object> actual = serializer.toMap(obj);

        assertEquals(expected, actual);
        verify(om).convertValue(eq(obj), any(TypeReference.class));
    }

    @Test
    void toMap_shouldThrowIllegalState_whenConvertFails() {
        ObjectMapper om = mock(ObjectMapper.class);
        OutboxSerializer serializer = new OutboxSerializer(om);

        Dummy obj = new Dummy(1, "a");
        when(om.convertValue(eq(obj), any(TypeReference.class)))
                .thenThrow(new IllegalArgumentException("boom"));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> serializer.toMap(obj));

        assertEquals("Failed to convert outbox payload to map", ex.getMessage());
        assertNotNull(ex.getCause());
        verify(om).convertValue(eq(obj), any(TypeReference.class));
    }

    @Test
    void fromJson_shouldDeserialize() throws Exception {
        ObjectMapper om = mock(ObjectMapper.class);
        OutboxSerializer serializer = new OutboxSerializer(om);

        String json = "{\"a\":1,\"b\":\"x\"}";
        Dummy dto = new Dummy(1, "x");

        when(om.readValue(json, Dummy.class)).thenReturn(dto);

        Dummy result = serializer.fromJson(json, Dummy.class);

        assertNotNull(result);
        assertEquals(1, result.a());
        assertEquals("x", result.b());
        verify(om).readValue(json, Dummy.class);
    }

    @Test
    void fromJson_shouldThrowIllegalState_whenJacksonFails() throws Exception {
        ObjectMapper om = mock(ObjectMapper.class);
        OutboxSerializer serializer = new OutboxSerializer(om);

        when(om.readValue(anyString(), eq(Dummy.class))).thenThrow(new RuntimeException("boom"));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> serializer.fromJson("{}", Dummy.class)
        );

        assertEquals("Failed to deserialize outbox payload", ex.getMessage());
        assertNotNull(ex.getCause());
        verify(om).readValue(anyString(), eq(Dummy.class));
    }

    @Test
    void fromMap_shouldConvertMapToDto() {
        ObjectMapper om = mock(ObjectMapper.class);
        OutboxSerializer serializer = new OutboxSerializer(om);

        Map<String, Object> payload = Map.of("a", 7, "b", "y");
        Dummy expected = new Dummy(7, "y");

        when(om.convertValue(payload, Dummy.class)).thenReturn(expected);

        Dummy actual = serializer.fromMap(payload, Dummy.class);

        assertNotNull(actual);
        assertEquals(7, actual.a());
        assertEquals("y", actual.b());
        verify(om).convertValue(payload, Dummy.class);
    }

    @Test
    void fromMap_shouldThrowIllegalState_whenConvertFails() {
        ObjectMapper om = mock(ObjectMapper.class);
        OutboxSerializer serializer = new OutboxSerializer(om);

        Map<String, Object> payload = Map.of("a", 1);
        when(om.convertValue(payload, Dummy.class)).thenThrow(new IllegalArgumentException("boom"));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> serializer.fromMap(payload, Dummy.class)
        );

        assertEquals("Failed to convert outbox payload map", ex.getMessage());
        assertNotNull(ex.getCause());
        verify(om).convertValue(payload, Dummy.class);
    }

    // simple DTO for tests
    record Dummy(int a, String b) {}
}