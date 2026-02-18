package com.java_new.concurrency.context;

public class ThreadLocalComparison {

    private static final ThreadLocal<String> THREAD_LOCAL_REQUEST_ID = new ThreadLocal<>();

    public static void demonstrateThreadLocalLeak() {
        THREAD_LOCAL_REQUEST_ID.set("request-123");
        String value = THREAD_LOCAL_REQUEST_ID.get();
        System.out.println("ThreadLocal value: " + value);
        // Problem: if we forget remove(), value leaks to next request
        // THREAD_LOCAL_REQUEST_ID.remove();
    }

    public static void demonstrateScopedValueSolution() {
        ScopedValue<String> scopedRequestId = ScopedValue.newInstance();

        ScopedValue.where(scopedRequestId, "request-789").run(() -> {
            System.out.println("Scoped value: " + scopedRequestId.get());

            Thread.startVirtualThread(() -> {
                System.out.println("Scoped value in virtual thread: " + scopedRequestId.get());
            });
        });

        System.out.println("Is bound after scope: " + scopedRequestId.isBound());
    }
}

