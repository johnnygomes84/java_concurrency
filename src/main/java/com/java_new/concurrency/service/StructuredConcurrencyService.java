package com.java_new.concurrency.service;

import com.java_new.concurrency.context.RequestContext;
import com.java_new.concurrency.model.DemoResponse;
import com.java_new.concurrency.model.DemoResponse.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.StructuredTaskScope.Subtask;

@Service
public class StructuredConcurrencyService {

    private final ScopedValueService scopedValueService;

    public StructuredConcurrencyService(ScopedValueService scopedValueService) {
        this.scopedValueService = scopedValueService;
    }

    public DemoResponse runFullDemo() {
        String requestId = UUID.randomUUID().toString();

        try {
            return ScopedValue
                .where(RequestContext.REQUEST_ID, requestId)
                .where(RequestContext.TENANT_ID, "tenant-demo")
                .where(RequestContext.USER_ID, "user-java25")
                .call(() -> buildDemoResponse(requestId));
        } catch (Exception e) {
            throw new RuntimeException("Demo failed", e);
        }
    }

    private DemoResponse buildDemoResponse(String requestId) throws Exception {
        SuccessFanOut successFanOut = demonstrateSuccessFanOut();
        FailurePropagation failurePropagation = demonstrateFailurePropagation();
        Timeout timeout = demonstrateTimeout();

        StructuredConcurrency structuredConcurrency = new StructuredConcurrency(
            successFanOut, failurePropagation, timeout
        );

        boolean visibleInDeepCall = scopedValueService.demonstrateDeepCallAccess();
        boolean visibleInChildTasks = demonstrateScopedValueInSubtasks();

        ScopedValues scopedValues = new ScopedValues(visibleInDeepCall, visibleInChildTasks);
        Features features = new Features(structuredConcurrency, scopedValues);

        String javaVersion = System.getProperty("java.version");
        return new DemoResponse(javaVersion, requestId, features);
    }

    public SuccessFanOut demonstrateSuccessFanOut() throws InterruptedException, ExecutionException {
        try (var scope = StructuredTaskScope.open()) {
            Subtask<String> profileTask = scope.fork(this::fetchProfile);
            Subtask<String> limitsTask = scope.fork(this::fetchLimits);
            Subtask<String> pricingTask = scope.fork(this::fetchPricing);

            scope.join();

            Map<String, String> results = Map.of(
                "profile", profileTask.get(),
                "limits", limitsTask.get(),
                "pricing", pricingTask.get()
            );

            return new SuccessFanOut(new String[]{"profile", "limits", "pricing"}, results);
        }
    }

    public FailurePropagation demonstrateFailurePropagation() {
        try (var scope = StructuredTaskScope.open()) {
            Subtask<String> profileTask = scope.fork(this::fetchProfile);
            Subtask<String> limitsTask = scope.fork(this::fetchLimits);
            Subtask<String> pricingTask = scope.fork(this::fetchPricingWithFailure);

            scope.join();

            boolean siblingsCancelled =
                profileTask.state() == Subtask.State.UNAVAILABLE ||
                limitsTask.state() == Subtask.State.UNAVAILABLE;

            return new FailurePropagation("pricing", siblingsCancelled);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new FailurePropagation("pricing", true);
        }
    }

    public Timeout demonstrateTimeout() {
        int deadlineMs = 200;
        List<String> completed = new ArrayList<>();
        List<String> cancelled = new ArrayList<>();

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var profileFuture = executor.submit(this::fetchProfileFast);
            var limitsFuture = executor.submit(this::fetchLimitsSlow);
            var pricingFuture = executor.submit(this::fetchPricingSlow);

            try {
                profileFuture.get(deadlineMs, TimeUnit.MILLISECONDS);
                completed.add("profile");
            } catch (TimeoutException e) {
                profileFuture.cancel(true);
                cancelled.add("profile");
            }

            try {
                limitsFuture.get(deadlineMs - 100, TimeUnit.MILLISECONDS);
                completed.add("limits");
            } catch (TimeoutException e) {
                limitsFuture.cancel(true);
                cancelled.add("limits");
            }

            try {
                pricingFuture.get(deadlineMs - 100, TimeUnit.MILLISECONDS);
                completed.add("pricing");
            } catch (TimeoutException e) {
                pricingFuture.cancel(true);
                cancelled.add("pricing");
            }

        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
        }

        return new Timeout(deadlineMs, completed.toArray(new String[0]), cancelled.toArray(new String[0]));
    }

    public boolean demonstrateScopedValueInSubtasks() throws InterruptedException, ExecutionException {
        try (var scope = StructuredTaskScope.open()) {
            Subtask<Boolean> task = scope.fork(() -> {
                String requestId = RequestContext.getRequestId();
                return requestId != null && !requestId.equals("none");
            });

            scope.join();
            return task.get();
        }
    }

    private String fetchProfile() throws InterruptedException {
        Thread.sleep(50);
        return "OK (requestId: " + RequestContext.getRequestId() + ")";
    }

    private String fetchLimits() throws InterruptedException {
        Thread.sleep(100);
        return "OK";
    }

    private String fetchPricing() throws InterruptedException {
        Thread.sleep(75);
        return "OK";
    }

    private String fetchPricingWithFailure() throws InterruptedException {
        Thread.sleep(50);
        throw new RuntimeException("Pricing service unavailable");
    }

    private String fetchProfileFast() throws InterruptedException {
        Thread.sleep(50);
        return "OK";
    }

    private String fetchLimitsSlow() throws InterruptedException {
        Thread.sleep(500);
        return "OK";
    }

    private String fetchPricingSlow() throws InterruptedException {
        Thread.sleep(600);
        return "OK";
    }
}

