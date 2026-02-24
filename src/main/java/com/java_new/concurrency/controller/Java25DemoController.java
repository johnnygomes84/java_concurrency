package com.java_new.concurrency.controller;

import com.java_new.concurrency.model.DemoResponse;
import com.java_new.concurrency.model.ScopedValuesResponse;
import com.java_new.concurrency.service.ScopedValueService;
import com.java_new.concurrency.service.StructuredConcurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/java25")
@RequiredArgsConstructor
public class Java25DemoController {

    private final StructuredConcurrencyService structuredConcurrencyService;
    private final ScopedValueService scopedValueService;

    @GetMapping("/demo")
    public ResponseEntity<DemoResponse> demo() {
        DemoResponse response = structuredConcurrencyService.runFullDemo();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/scoped-values")
    public ResponseEntity<ScopedValuesResponse> scopedValuesDemo() {
        ScopedValuesResponse result = scopedValueService.runScopedValuesDemo();
        return ResponseEntity.ok(result);
    }
}
