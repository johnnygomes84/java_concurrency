# Java 25 Features

## Structured Concurrency (JEP 505)

Treats multiple concurrent tasks as a single unit of work.

**Benefits:**
- Automatic lifecycle management
- Error propagation
- Sibling task cancellation
- No thread leaks

**API:**
```java
try (var scope = StructuredTaskScope.open()) {
    Subtask<String> task1 = scope.fork(() -> fetchData1());
    Subtask<String> task2 = scope.fork(() -> fetchData2());
    scope.join();
    return task1.get() + task2.get();
}
```

## Scoped Values (JEP 506)

Safer alternative to ThreadLocal for sharing data across threads.

**Benefits:**
- Automatic cleanup (no memory leaks)
- Immutable by design
- Propagates to child tasks automatically
- Optimized for virtual threads

**API:**
```java
static final ScopedValue<String> REQUEST_ID = ScopedValue.newInstance();

ScopedValue.where(REQUEST_ID, "abc-123").run(() -> {
    String id = REQUEST_ID.get(); // works anywhere in call stack
});
```

## vs Old Approaches

| Feature | ThreadLocal | ScopedValue |
|---------|-------------|-------------|
| Cleanup | Manual | Automatic |
| Mutability | Mutable | Immutable |
| Virtual threads | Poor | Optimized |

| Feature | ExecutorService | StructuredTaskScope |
|---------|----------------|---------------------|
| Cancellation | Manual | Automatic |
| Error propagation | Manual | Automatic |
| Lifecycle | Manual | Managed |

