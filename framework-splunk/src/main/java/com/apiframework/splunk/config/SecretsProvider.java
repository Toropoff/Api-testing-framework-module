/*
 * Temporary location: SecretsProvider and EnvSecretsProvider were moved here from
 * framework-core during the auth subsystem removal (see CLAUDE.md changelog).
 * These interfaces belong in framework-core as part of a broader credential
 * management redesign. When that work is done, move them back and update
 * SplunkConnectionConfig to reference the core module versions.
 */
package com.apiframework.splunk.config;

import java.util.Optional;

/**
 * SPI for resolving secret values (passwords, tokens, API keys) by key.
 * Part of the production integration interface — used when the framework
 * runs against real environments with vault-based or env-var-based credential stores.
 *
 * <p>Not called by the default dev/test config path ({@link com.apiframework.config.ConfigResolver#resolveFromSystem()}).
 * Activated when {@link SplunkConnectionConfig#fromSystem(java.util.List)} is used directly.
 */
public interface SecretsProvider {
    Optional<String> getSecret(String key);
}
