package com.epam.infrastructure.mappers;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CommonMapperTest {

    @Test
    void uuidToString_shouldReturnStringOrNull() {
        UUID uuid = UUID.randomUUID();
        assertEquals(uuid.toString(), CommonMapper.uuidToString(uuid));
        assertNull(CommonMapper.uuidToString(null));
    }

    @Test
    void stringToUuid_shouldReturnUuidOrNull() {
        UUID uuid = UUID.randomUUID();
        String uuidStr = uuid.toString();
        assertEquals(uuid, CommonMapper.stringToUuid(uuidStr));
        assertNull(CommonMapper.stringToUuid(null));
    }

    @Test
    void parseLocalDateTime_shouldParseVariousFormats() {
        LocalDateTime expected = LocalDateTime.of(2025, 11, 27, 16, 30, 15);

        assertEquals(expected, CommonMapper.parseLocalDateTime("2025-11-27T16:30:15"));
        assertEquals(LocalDateTime.of(2025, 11, 27, 16, 30),
                CommonMapper.parseLocalDateTime("2025-11-27 16:30"));
        assertEquals(expected, CommonMapper.parseLocalDateTime("2025-11-27 16:30:15"));
        assertEquals(LocalDateTime.of(2025, 11, 27, 16, 30),
                CommonMapper.parseLocalDateTime("2025/11/27 16:30"));
        assertEquals(expected, CommonMapper.parseLocalDateTime("2025/11/27 16:30:15"));
        assertEquals(LocalDateTime.of(2025, 11, 27, 16, 30),
                CommonMapper.parseLocalDateTime("2025:11:27 16:30"));
        assertEquals(expected, CommonMapper.parseLocalDateTime("2025:11:27 16:30:15"));

        assertNull(CommonMapper.parseLocalDateTime(null));
        assertNull(CommonMapper.parseLocalDateTime("  "));

        assertThrows(IllegalArgumentException.class,
                () -> CommonMapper.parseLocalDateTime("invalid-date"));
    }

    @Test
    void formatLocalDateTime_shouldReturnFormattedStringOrNull() {
        LocalDateTime dateTime = LocalDateTime.of(2025, 11, 27, 16, 30, 15);
        assertEquals("2025-11-27 16:30:15", CommonMapper.formatLocalDateTime(dateTime));
        assertNull(CommonMapper.formatLocalDateTime(null));
    }

    @Test
    void parseLocalDate_shouldParseVariousFormats() {
        LocalDate expected = LocalDate.of(2025, 11, 27);

        assertEquals(expected, CommonMapper.parseLocalDate("2025-11-27")); // ISO
        assertEquals(expected, CommonMapper.parseLocalDate("2025/11/27"));
        assertEquals(expected, CommonMapper.parseLocalDate("2025:11:27"));
        assertEquals(expected, CommonMapper.parseLocalDate("2025-11-27")); // explicit pattern

        assertNull(CommonMapper.parseLocalDate(null));
        assertNull(CommonMapper.parseLocalDate("  "));

        assertThrows(IllegalArgumentException.class,
                () -> CommonMapper.parseLocalDate("invalid-date"));
    }

    @Test
    void formatLocalDate_shouldReturnFormattedStringOrNull() {
        LocalDate date = LocalDate.of(2025, 11, 27);
        assertEquals("2025-11-27", CommonMapper.formatLocalDate(date));
        assertNull(CommonMapper.formatLocalDate(null));
    }
}
