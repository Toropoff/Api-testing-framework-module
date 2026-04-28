package com.apiframework.config;

public final class ConfigResolver {

    private ConfigResolver() {}

    public static FrameworkRuntimeConfig resolveFromSystem() {
        var config = new FrameworkRuntimeConfig(
            EnvResolver.string("FRAMEWORK_ENV", "dev"),
            EnvResolver.required("FRAMEWORK_BASE_URL"),
            EnvResolver.integer("FRAMEWORK_CONNECT_TIMEOUT", 5000),
            EnvResolver.integer("FRAMEWORK_READ_TIMEOUT", 15000),
            EnvResolver.string("FRAMEWORK_CLIENT_NAME", ""),
            EnvResolver.string("FRAMEWORK_CLIENT_SECRET", "")
        );
        System.out.println(
            "Framework config: env=" + config.env() +
            ", baseUrl=" + config.baseUrl() +
            ", timeouts=" + config.connectTimeoutMs() + "/" + config.readTimeoutMs()
        );
        return config;
    }
}
