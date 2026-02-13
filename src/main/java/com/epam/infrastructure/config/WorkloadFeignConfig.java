package com.epam.infrastructure.config;

import com.epam.infrastructure.logging.TransactionIdFilter;
import com.epam.infrastructure.security.auth.InternalTokenService;
import feign.RequestInterceptor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorkloadFeignConfig {

    @Bean
    public RequestInterceptor transactionIdInterceptor() {
        return template -> {
            String txId = MDC.get(TransactionIdFilter.TRANSACTION_ID_HEADER);
            if (txId != null) template.header(TransactionIdFilter.TRANSACTION_ID_HEADER, txId);
        };
    }

    @Bean
    public RequestInterceptor authInterceptor(InternalTokenService tokenService) {
        return template -> template.header("Authorization", "Bearer " + tokenService.issueServiceToken());
    }
}
