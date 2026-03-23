package com.apiframework.core.config;

import java.util.Optional;

/**
 * SPI for resolving secret values (passwords, tokens, API keys) by key.
 * Part of the production integration interface — used when the framework
 * runs against real environments with vault-based or env-var-based credential stores.
 *
 * <p>Not called by the default dev/test config path ({@link ConfigResolver#resolveFromSystem()}).
 * Activated when {@link ConfigResolver#resolve(EnvironmentProfile, java.util.List)} is used directly.
 */
public interface SecretsProvider {
    Optional<String> getSecret(String key);
}
