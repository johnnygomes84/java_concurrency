package com.java_new.concurrency.controller;

import com.java_new.concurrency.model.DemoResponse;
import com.java_new.concurrency.service.ScopedValueService;
import com.java_new.concurrency.service.StructuredConcurrencyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/java25")
public class Java25DemoController {

    private final StructuredConcurrencyService structuredConcurrencyService;
    private final ScopedValueService scopedValueService;

    public Java25DemoController(StructuredConcurrencyService structuredConcurrencyService,
                                ScopedValueService scopedValueService) {
        this.structuredConcurrencyService = structuredConcurrencyService;
        this.scopedValueService = scopedValueService;
    }

    @GetMapping("/demo")
    public DemoResponse demo() {
        return structuredConcurrencyService.runFullDemo();
    }

    @GetMapping("/scoped-values")
    public String scopedValuesDemo() {
        return scopedValueService.runScopedValuesDemo();
    }
}

