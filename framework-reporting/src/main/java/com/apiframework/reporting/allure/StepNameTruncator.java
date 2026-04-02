package com.apiframework.reporting.allure;

import io.qameta.allure.listener.StepLifecycleListener;
import io.qameta.allure.model.StepResult;

/**
 * Allure {@link StepLifecycleListener} that cleans up step noise produced by allure-assertj.
 *
 * <h3>Truncation</h3>
 * <p>allure-assertj names steps by calling {@code ObjectUtils.toString()} on the value passed
 * to {@code assertThat()}.  For arrays/collections this is a full {@code Arrays.toString()}
 * dump — an unreadable wall of text.  Names longer than {@value #MAX_NAME_LENGTH} characters
 * are truncated with {@code …}.
 *
 * <h3>Duplicate sub-step removal</h3>
 * <p>AspectJ intercepts assertion chain methods (e.g. {@code isEqualTo}) both at the
 * user-code callsite and again inside AssertJ's own implementation, producing a self-contained
 * duplicate: {@code isEqualTo 'X' → isEqualTo 'X'}.  In {@link #beforeStepStop} any child
 * whose name equals the parent's name is removed before the step is written to the result file.
 *
 * <p>Registered via {@code META-INF/services/io.qameta.allure.listener.StepLifecycleListener}
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

    /**
     * Removes child steps that are exact duplicates of the parent step name.
     * These are artefacts of AspectJ double-interception inside AssertJ internals.
     */
    @Override
    public void beforeStepStop(StepResult result) {
        String name = result.getName();
        if (name == null || result.getSteps().isEmpty()) {
            return;
        }
        result.getSteps().removeIf(child -> name.equals(child.getName()));
    }
}
