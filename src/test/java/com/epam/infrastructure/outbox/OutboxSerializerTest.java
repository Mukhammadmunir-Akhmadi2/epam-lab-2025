package com.epam.infrastructure.outbox;

import com.epam.infrastructure.outbox.util.OutboxSerializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
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
    }

    @Test
    void fromJson_shouldDeserialize() throws Exception {
        ObjectMapper om = mock(ObjectMapper.class);
        OutboxSerializer serializer = new OutboxSerializer(om);

        String json = "{\"a\":1}";
        Dummy dto = new Dummy();
        dto.a = 1;

        when(om.readValue(json, Dummy.class)).thenReturn(dto);

        Dummy result = serializer.fromJson(json, Dummy.class);

        assertNotNull(result);
        assertEquals(1, result.a);
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
    }

    static class Dummy {
        int a;
    }
}
