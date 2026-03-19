package com.apiframework.testsupport.base;

import java.time.Instant;
import java.util.Map;

public record TestExecutionContext(
    String testId,
    String correlationId,
    Instant startedAt,
    Map<String, String> environmentTags
) {
}
