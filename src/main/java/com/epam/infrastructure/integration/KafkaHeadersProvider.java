package com.epam.infrastructure.integration;

import com.epam.infrastructure.logging.TransactionIdFilter;
import com.epam.infrastructure.security.auth.InternalTokenService;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaHeadersProvider {

    public static final String AUTH_HEADER = "Authorization";

    private final InternalTokenService tokenService;

    public String currentTransactionId() {
        return MDC.get(TransactionIdFilter.TRANSACTION_ID_HEADER);
    }

    public String innerServerAuthorizationValue() {
        return "Bearer " + tokenService.issueServiceToken();
    }
}