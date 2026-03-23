package com.apiframework.core.config;

import com.apiframework.core.model.HttpRetryPolicy;
import org.aeonbits.owner.ConfigFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class ConfigResolver {
    private ConfigResolver() {
    }

    /**
     * Dev/test config path — reads all values directly from system properties
     * with sensible defaults. No property files, no secrets providers, no Owner library.
     * This is the path used by {@link com.apiframework.testsupport.base.BaseApiTest}.
     */
    public static FrameworkRuntimeConfig resolveFromSystem() {
        return new FrameworkRuntimeConfig(
            System.getProperty("framework.profile", "dev"),
            Integer.getInteger("http.connectTimeoutMs", 5000),
            Integer.getInteger("http.readTimeoutMs", 15000),
            new HttpRetryPolicy(
                Integer.getInteger("http.retry.maxAttempts", 1),
                Duration.ofMillis(Long.getLong("http.retry.delayMs", 0L)),
                Set.of(429, 500, 502, 503, 504)
            ),
            System.getProperty("auth.basic.username", ""),
            System.getProperty("auth.basic.password", "")
        );
    }

    /**
     * Production config path — resolves configuration from profile-specific property files
     * via the Owner library, and credentials from a {@link SecretsProvider} chain
     * (e.g. {@link EnvSecretsProvider} for env-var-based secrets).
     *
     * <p>Use this method when running against real environments where credentials
     * come from a vault or env vars and config differs per profile (dev/stage/prod).
     */
    public static FrameworkRuntimeConfig resolve(EnvironmentProfile profile, List<SecretsProvider> providers) {
        ConfigFactory.setProperty("framework.profile", profile.id());
        FrameworkProperties properties = ConfigFactory.create(FrameworkProperties.class, System.getProperties());

        List<SecretsProvider> chain = new ArrayList<>();
        chain.addAll(providers);
        chain.add(new EnvSecretsProvider());

        String basicUsername = resolveSecret(properties.basicUsernameSecretKey(), chain)
            .orElseGet(() -> System.getProperty("auth.basic.username", ""));
        String basicPassword = resolveSecret(properties.basicPasswordSecretKey(), chain)
            .orElseGet(() -> System.getProperty("auth.basic.password", ""));

        HttpRetryPolicy retryPolicy = new HttpRetryPolicy(
            properties.retryMaxAttempts(),
            Duration.ofMillis(properties.retryDelayMs()),
            Set.of(429, 500, 502, 503, 504)
        );

        return new FrameworkRuntimeConfig(
            profile.id(),
            properties.connectTimeoutMs(),
            properties.readTimeoutMs(),
            retryPolicy,
            basicUsername,
            basicPassword
        );
    }

    @SafeVarargs
    public static FrameworkRuntimeConfig resolve(EnvironmentProfile profile, SecretsProvider... providers) {
        return resolve(profile, Arrays.asList(providers));
    }

    private static Optional<String> resolveSecret(String key, List<SecretsProvider> providers) {
        return providers.stream()
            .map(provider -> provider.getSecret(key))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst();
    }
}
