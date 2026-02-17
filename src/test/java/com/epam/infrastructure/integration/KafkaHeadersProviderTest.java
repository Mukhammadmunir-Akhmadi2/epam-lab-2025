package com.epam.infrastructure.integration;

import com.epam.infrastructure.logging.TransactionIdFilter;
import com.epam.infrastructure.security.auth.InternalTokenService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KafkaHeadersProviderTest {

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void currentTransactionId_shouldReturnValueFromMdc() {
        InternalTokenService tokenService = mock(InternalTokenService.class);
        KafkaHeadersProvider provider = new KafkaHeadersProvider(tokenService);

        MDC.put(TransactionIdFilter.TRANSACTION_ID_HEADER, "tx-123");

        assertEquals("tx-123", provider.currentTransactionId());
        verifyNoInteractions(tokenService);
    }

    @Test
    void currentTransactionId_shouldReturnNull_whenMissingInMdc() {
        InternalTokenService tokenService = mock(InternalTokenService.class);
        KafkaHeadersProvider provider = new KafkaHeadersProvider(tokenService);

        assertNull(provider.currentTransactionId());
        verifyNoInteractions(tokenService);
    }

    @Test
    void currentAuthorizationValue_shouldReturnBearerToken_fromInternalTokenService() {
        InternalTokenService tokenService = mock(InternalTokenService.class);
        when(tokenService.issueServiceToken()).thenReturn("abc.def.ghi");

        KafkaHeadersProvider provider = new KafkaHeadersProvider(tokenService);

        String auth = provider.currentAuthorizationValue();

        assertEquals("Bearer abc.def.ghi", auth);
        verify(tokenService, times(1)).issueServiceToken();
    }
}
