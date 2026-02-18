package com.java_new.concurrency.model;

import java.util.Map;

public record DemoResponse(
    String javaVersion,
    String requestId,
    Features features
) {
    public record Features(
        StructuredConcurrency structuredConcurrency,
        ScopedValues scopedValues
    ) {}

    public record StructuredConcurrency(
        SuccessFanOut successFanOut,
        FailurePropagation failurePropagation,
        Timeout timeout
    ) {}

    public record SuccessFanOut(
        String[] tasks,
        Map<String, String> result
    ) {}

    public record FailurePropagation(
        String failedTask,
        boolean siblingCancelled
    ) {}

    public record Timeout(
        int deadlineMs,
        String[] completed,
        String[] cancelled
    ) {}

    public record ScopedValues(
        boolean visibleInDeepCall,
        boolean visibleInChildTasks
    ) {}
}

