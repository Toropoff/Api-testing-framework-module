package com.apiframework.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

/**
 *a lightweight, centralized utility for loading domain-specific basePath
 *from environment-aware property files with fallback and validation
 */
public final class DomainConfig {

    private DomainConfig() {
    }

    /**
     * Loads the {@code basePath} property using an env-aware lookup.
     * Tries {@code {basename}.{env}.properties} first (e.g. {@code postman-echo.uat.properties}),
     * then falls back to {@code propertiesFile}.
     *
     * @param contextClass   class whose classloader is used to locate the resource
     * @param propertiesFile classpath resource name, e.g. {@code "postman-echo.properties"}
     * @param env            environment name, e.g. {@code "uat"}
     * @return the resolved base path, never null
     */
    public static String loadBasePath(Class<?> contextClass, String propertiesFile, String env) {
        if (env != null && !env.isBlank()) {
            String envFile = propertiesFile.replace(".properties", "." + env + ".properties");
            try (InputStream in = contextClass.getClassLoader().getResourceAsStream(envFile)) {
                if (in != null) {
                    Properties props = new Properties();
                    props.load(in);
                    String basePath = props.getProperty("basePath");
                    if (basePath != null) {
                        return basePath;
                    }
                }
            } catch (IOException ignored) {
                // fall through to default file
            }
        }
        return loadBasePath(contextClass, propertiesFile);
    }

    /**
     * Loads the {@code basePath} property from the given classpath resource.
     *
     * @param contextClass   class whose classloader is used to locate the resource
     * @param propertiesFile classpath resource name, e.g. {@code "postman-echo.properties"}
     * @return the resolved base path, never null
     * @throws IllegalStateException if the file is missing or basePath is not set
     */
    public static String loadBasePath(Class<?> contextClass, String propertiesFile) {
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
            props.getProperty("basePath"),
            "basePath not set in " + propertiesFile
        );
    }

    /**
     * Loads the {@code apiName} property from the given classpath resource.
     * Unlike {@code basePath}, {@code apiName} is environment-independent — it is a stable
     * display label used for reporting (e.g. Allure parentSuite).
     *
     * @param contextClass   class whose classloader is used to locate the resource
     * @param propertiesFile classpath resource name, e.g. {@code "postman-echo.properties"}
     * @return the resolved api name, never null
     * @throws IllegalStateException if the file is missing or apiName is not set
     */
    public static String loadApiName(Class<?> contextClass, String propertiesFile) {
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
            props.getProperty("apiName"),
            "apiName not set in " + propertiesFile
        );
    }
}
