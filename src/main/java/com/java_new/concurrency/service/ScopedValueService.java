package com.java_new.concurrency.service;

import com.java_new.concurrency.context.RequestContext;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ScopedValueService {

    public boolean demonstrateDeepCallAccess() {
        String result = accessFromDeepCall();
        return result.contains(RequestContext.getRequestId());
    }

    public String runScopedValuesDemo() {
        String requestId = UUID.randomUUID().toString();

        try {
            return ScopedValue
                .where(RequestContext.REQUEST_ID, requestId)
                .where(RequestContext.TENANT_ID, "tenant-123")
                .where(RequestContext.USER_ID, "user-456")
                .call(() -> {
                    String businessResult = processBusinessLogic();
                    String asyncResult = demonstrateAsyncProcessing();
                    return String.format(
                        "Scoped Values Demo:\nBusiness Logic: %s\nAsync Processing: %s",
                        businessResult, asyncResult
                    );
                });
        } catch (Exception e) {
            throw new RuntimeException("Scoped values demo failed", e);
        }
    }

    public String processBusinessLogic() {
        String requestId = RequestContext.getRequestId();
        String tenantId = RequestContext.getTenantId();
        String userId = RequestContext.getUserId();
        String data = callRepositoryLayer();

        return String.format("Processed by user=%s, tenant=%s, request=%s, data=%s",
            userId, tenantId, requestId, data);
    }

    private String callRepositoryLayer() {
        return "data-" + RequestContext.getRequestId();
    }

    public String demonstrateAsyncProcessing() throws InterruptedException {
        var result = new StringBuilder();
        Thread virtualThread = Thread.startVirtualThread(() -> {
            String virtualThreadRequestId = RequestContext.getRequestId();
            result.append("Virtual thread sees: ").append(virtualThreadRequestId);
        });

        virtualThread.join();
        return result.toString();
    }

    private String accessFromDeepCall() {
        return deepMethod1();
    }

    private String deepMethod1() {
        return deepMethod2();
    }

    private String deepMethod2() {
        return deepMethod3();
    }

    private String deepMethod3() {
        return "RequestID from deep call: " + RequestContext.getRequestId();
    }
}

