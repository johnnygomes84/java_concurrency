package com.java_new.concurrency.service;

import com.java_new.concurrency.context.RequestContext;
import com.java_new.concurrency.model.ScopedValuesResponse;
import org.springframework.stereotype.Service;

@Service
public class ScopedValueService {

    public boolean demonstrateDeepCallAccess() {
        String result = accessFromDeepCall();
        return result.contains(RequestContext.getRequestId());
    }

    public ScopedValuesResponse runScopedValuesDemo() {
        String requestId = java.util.UUID.randomUUID().toString();
        try {
            return ScopedValue
                .where(RequestContext.REQUEST_ID, requestId)
                .where(RequestContext.TENANT_ID, "tenant-123")
                .where(RequestContext.USER_ID, "user-456")
                .call(() -> {
                    String businessResult = "Business logic executed";
                    String asyncResult = demonstrateAsyncProcessingWithScopedValue();
                    return new ScopedValuesResponse(
                        RequestContext.getRequestId(),
                        RequestContext.getTenantId(),
                        RequestContext.getUserId(),
                        businessResult,
                        asyncResult
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

    public String demonstrateAsyncProcessingWithScopedValue() {
        try (var scope = java.util.concurrent.StructuredTaskScope.open()) {
            var subtask = scope.fork(() -> {
                return "Virtual thread sees: " + RequestContext.getRequestId();
            });
            scope.join();
            return subtask.get();
        } catch (Exception e) {
            return "Virtual thread error: " + e.getMessage();
        }
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
