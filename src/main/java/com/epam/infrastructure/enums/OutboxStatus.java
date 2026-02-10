package com.epam.infrastructure.enums;

public enum OutboxStatus {
    NEW,
    RETRY,
    SENT,
    DEAD
}
