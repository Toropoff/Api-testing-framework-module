package com.apiframework.config;

import java.util.Locale;
import java.util.Optional;

/**
 * Resolves secrets from environment variables with a configurable prefix.
 * Part of the production integration interface — reads credentials from
 * env vars like {@code FRAMEWORK_SECRET_AUTH_BASIC_PASSWORD}.
 *
 * <p>Not called by the default dev/test config path ({@link ConfigResolver#resolveFromSystem()}).
 * Activated when {@link ConfigResolver#resolve(EnvironmentProfile, java.util.List)} is used directly.
 */
public final class EnvSecretsProvider implements SecretsProvider {
    private final String prefix;

    public EnvSecretsProvider() {
        this("FRAMEWORK_SECRET_");
    }

    public EnvSecretsProvider(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public Optional<String> getSecret(String key) {
        String normalized = key.toUpperCase(Locale.ROOT).replace('.', '_');
        return Optional.ofNullable(System.getenv(prefix + normalized));
    }
}
