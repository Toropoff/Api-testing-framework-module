/*
 * Temporary location: SecretsProvider and EnvSecretsProvider were moved here from
 * framework-core during the auth subsystem removal (see CLAUDE.md changelog).
 * These interfaces belong in framework-core as part of a broader credential
 * management redesign. When that work is done, move them back and update
 * SplunkConnectionConfig to reference the core module versions.
 */
package com.apiframework.splunk.config;

import java.util.Locale;
import java.util.Optional;

/**
 * Resolves secrets from environment variables with a configurable prefix.
 * Part of the production integration interface — reads credentials from
 * env vars like {@code FRAMEWORK_SECRET_SPLUNK_PASSWORD}.
 *
 * <p>Not called by the default dev/test config path ({@link com.apiframework.config.ConfigResolver#resolveFromSystem()}).
 * Activated when {@link SplunkConnectionConfig#fromSystem(java.util.List)} is used directly.
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
