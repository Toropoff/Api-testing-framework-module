package com.apiframework.reporting.allure;

import io.qameta.allure.listener.StepLifecycleListener;
import io.qameta.allure.model.StepResult;

/**
 * Allure {@link StepLifecycleListener} that truncates step names longer than
 * {@value #MAX_NAME_LENGTH} characters before they are written to the result file.
 *
 * <p>allure-assertj generates step names by calling {@code ObjectUtils.toString()} on the
 * actual value passed to {@code assertThat()}.  For array or collection arguments this
 * produces the full {@code Arrays.toString()} output, which becomes an unreadable wall of
 * text in the report.  Truncation keeps the name scannable while preserving context.
 *
 * <p>Registered via {@code META-INF/services/io.qameta.allure.listener.LifecycleListener}
 * so Allure picks it up automatically through {@code ServiceLoader}.
 */
public final class StepNameTruncator implements StepLifecycleListener {

    static final int MAX_NAME_LENGTH = 120;
    private static final String ELLIPSIS = "…";

    @Override
    public void beforeStepStart(StepResult result) {
        String name = result.getName();
        if (name != null && name.length() > MAX_NAME_LENGTH) {
            result.setName(name.substring(0, MAX_NAME_LENGTH) + ELLIPSIS);
        }
    }
}
