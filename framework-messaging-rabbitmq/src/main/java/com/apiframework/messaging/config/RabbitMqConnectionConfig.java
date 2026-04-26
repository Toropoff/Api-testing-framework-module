package com.apiframework.messaging.config;

import com.apiframework.config.EnvResolver;

public record RabbitMqConnectionConfig(
    String host,
    int port,
    String virtualHost,
    String username,
    String password
) {

    public static RabbitMqConnectionConfig fromSystem() {
        return new RabbitMqConnectionConfig(
            EnvResolver.required("RABBITMQ_HOST"),
            EnvResolver.integer("RABBITMQ_PORT", 5672),
            EnvResolver.required("RABBITMQ_VHOST"),
            EnvResolver.required("RABBITMQ_USERNAME"),
            EnvResolver.required("RABBITMQ_PASSWORD")
        );
    }
}
