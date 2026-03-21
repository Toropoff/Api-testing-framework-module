package com.apiframework.reporting.allure;

import com.apiframework.core.util.ReflectiveFactory;

public record HttpReportingConfiguration(
    HttpStepNameStrategy stepNameStrategy,
    HttpAttachmentRenderer attachmentRenderer,
    HttpErrorAttachmentStrategy errorAttachmentStrategy
) {
    private static final String STEP_NAME_STRATEGY_CLASS = "framework.reporting.stepNameStrategyClass";
    private static final String ATTACHMENT_RENDERER_CLASS = "framework.reporting.attachmentRendererClass";
    private static final String ERROR_ATTACHMENT_STRATEGY_CLASS = "framework.reporting.errorAttachmentStrategyClass";
    private static final String ATTACHMENT_MAX_BYTES_PROPERTY = "framework.reporting.attachments.maxBytes";

    public static HttpReportingConfiguration defaultConfiguration(boolean attachmentsEnabled) {
        HttpAttachmentRenderer renderer = ReflectiveFactory.instantiateOrDefault(
            System.getProperty(ATTACHMENT_RENDERER_CLASS),
            HttpAttachmentRenderer.class,
            () -> new DefaultHttpAttachmentRenderer(attachmentsEnabled, Integer.getInteger(ATTACHMENT_MAX_BYTES_PROPERTY, 65_536))
        );

        return new HttpReportingConfiguration(
            ReflectiveFactory.instantiateOrDefault(
                System.getProperty(STEP_NAME_STRATEGY_CLASS),
                HttpStepNameStrategy.class,
                DefaultHttpStepNameStrategy::new),
            renderer,
            ReflectiveFactory.instantiateOrDefault(
                System.getProperty(ERROR_ATTACHMENT_STRATEGY_CLASS),
                HttpErrorAttachmentStrategy.class,
                DefaultHttpErrorAttachmentStrategy::new)
        );
    }
}
