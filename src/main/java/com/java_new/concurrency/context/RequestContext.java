package com.java_new.concurrency.context;

public class RequestContext {

    public static final ScopedValue<String> REQUEST_ID = ScopedValue.newInstance();
    public static final ScopedValue<String> TENANT_ID = ScopedValue.newInstance();
    public static final ScopedValue<String> USER_ID = ScopedValue.newInstance();

    public static String getRequestId() {
        return REQUEST_ID.isBound() ? REQUEST_ID.get() : "none";
    }

    public static String getTenantId() {
        return TENANT_ID.isBound() ? TENANT_ID.get() : "default";
    }

    public static String getUserId() {
        return USER_ID.isBound() ? USER_ID.get() : "anonymous";
    }
}

