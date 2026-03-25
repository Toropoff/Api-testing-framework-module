package com.apiframework.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;


/**
 * Utility for loading domain-level configuration from classpath property files.
 * Eliminates the repeated static initializer pattern across domain Api classes.
 */
public final class DomainConfig {

    private DomainConfig() {
    }

    /**
     * Loads the {@code baseUrl} property from the given classpath resource.
     *
     * @param contextClass   class whose classloader is used to locate the resource
     * @param propertiesFile classpath resource name, e.g. {@code "postman-echo.properties"}
     * @return the resolved base URL, never null
     * @throws IllegalStateException if the file is missing or baseUrl is not set
     */
    /**
     * Loads the {@code baseUrl} property using an env-aware lookup.
     * Tries {@code {basename}.{env}.properties} first (e.g. {@code postman-echo.uat.properties}),
     * then falls back to {@code propertiesFile}.
     *
     * @param contextClass   class whose classloader is used to locate the resource
     * @param propertiesFile classpath resource name, e.g. {@code "postman-echo.properties"}
     * @param env            environment name, e.g. {@code "uat"}
     * @return the resolved base URL, never null
     */
    public static String loadBaseUrl(Class<?> contextClass, String propertiesFile, String env) {
        if (env != null && !env.isBlank()) {
            String envFile = propertiesFile.replace(".properties", "." + env + ".properties");
            try (InputStream in = contextClass.getClassLoader().getResourceAsStream(envFile)) {
                if (in != null) {
                    Properties props = new Properties();
                    props.load(in);
                    String baseUrl = props.getProperty("baseUrl");
                    if (baseUrl != null) {
                        return baseUrl;
                    }
                }
            } catch (IOException ignored) {
                // fall through to default file
            }
        }
        return loadBaseUrl(contextClass, propertiesFile);
    }

    public static String loadBaseUrl(Class<?> contextClass, String propertiesFile) {
        Properties props = new Properties();
        try (InputStream in = contextClass.getClassLoader().getResourceAsStream(propertiesFile)) {
            if (in == null) {
                throw new IllegalStateException(propertiesFile + " not found on classpath");
            }
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load " + propertiesFile, e);
        }
        return Objects.requireNonNull(
            props.getProperty("baseUrl"),
            "baseUrl not set in " + propertiesFile
        );
    }
}
