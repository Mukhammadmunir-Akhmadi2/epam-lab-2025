package com.epam.application.tx;

public interface AfterCommitExecutor {
    void run(Runnable action);
}
