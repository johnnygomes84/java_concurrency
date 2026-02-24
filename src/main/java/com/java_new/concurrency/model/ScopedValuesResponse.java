package com.java_new.concurrency.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScopedValuesResponse {
    private String requestId;
    private String tenantId;
    private String userId;
    private String businessLogic;
    private String asyncProcessing;
}
