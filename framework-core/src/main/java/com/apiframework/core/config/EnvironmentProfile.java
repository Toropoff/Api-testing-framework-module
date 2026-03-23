package com.apiframework.core.config;

/**
 * Named environment profiles (dev, stage, prod) for profile-specific property file resolution.
 * Part of the production integration interface — selects which
 * {@code application-{profile}.properties} file to load via the Owner library.
 *
 * <p>Not called by the default dev/test config path ({@link ConfigResolver#resolveFromSystem()}).
 * Activated when {@link ConfigResolver#resolve(EnvironmentProfile, java.util.List)} is used directly.
 */
public enum EnvironmentProfile {
    DEV("dev"),
    STAGE("stage"),
    PROD("prod");

    private final String id;

    EnvironmentProfile(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public static EnvironmentProfile from(String raw) {
        for (EnvironmentProfile profile : values()) {
            if (profile.id.equalsIgnoreCase(raw)) {
                return profile;
            }
        }
        throw new IllegalArgumentException("Unsupported profile: " + raw);
    }
}
