package com.apiframework.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Lightweight config resolver for dev/local use.
 * Reads all values directly from system properties with sensible defaults.
 * No property files, no secrets providers.
 */
public final class ConfigResolver {
    private ConfigResolver() {
    }

    public static FrameworkRuntimeConfig resolveFromSystem() {
        String env = System.getenv("FRAMEWORK_ENV");
        if (env == null || env.isBlank()) {
            env = System.getProperty("framework.env", "dev");
        }

        String baseUrl = loadBaseUrl(env);

        return new FrameworkRuntimeConfig(
            // TODO: profile is never read by any caller (BaseApiTest uses baseUrl/timeouts, AllureTestNgListener uses env); candidate for removal
            System.getProperty("framework.profile", "dev"),
            env,
            baseUrl,
            Integer.getInteger("http.connectTimeoutMs", 5000),
            Integer.getInteger("http.readTimeoutMs", 15000)
        );
    }

    private static String loadBaseUrl(String env) {
        String file = "environments/" + env + ".properties";
        try (InputStream in = ConfigResolver.class.getClassLoader().getResourceAsStream(file)) {
            if (in == null) {
                return "";
            }
            Properties props = new Properties();
            props.load(in);
            // TODO: dead null-check — getProperty(key, default) never returns null; simplify to props.getProperty("baseUrl", "").trim()
            String baseUrl = props.getProperty("baseUrl", "");
            return baseUrl == null ? "" : baseUrl.trim();
        } catch (IOException e) {
            return "";
        }
    }
}
