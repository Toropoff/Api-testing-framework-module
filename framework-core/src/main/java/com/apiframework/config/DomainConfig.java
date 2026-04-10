package com.apiframework.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * Centralized utility for loading domain endpoint catalogs and metadata from
 * {@code {domainName}.properties} files on the classpath.
 */
public final class DomainConfig {

    private DomainConfig() {
    }

    /**
     * Loads all endpoint definitions from {@code {domainName}.properties} on the classpath.
     * Each endpoint is described by an {@code endpoints.{key}.method} and
     * {@code endpoints.{key}.relUrl} pair.
     *
     * @param domainName the domain identifier, e.g. {@code "open-holidays"}
     * @return immutable map of endpoint key → {@link EndpointDefinition}
     * @throws IllegalStateException if the file is missing or any endpoint definition is incomplete
     */
    public static Map<String, EndpointDefinition> loadEndpoints(String domainName) {
        Properties p = loadDomainProperties(domainName);
        Map<String, EndpointDefinition> out = new LinkedHashMap<>();
        for (String name : p.stringPropertyNames()) {
            if (!name.startsWith("endpoints.") || !name.endsWith(".method")) continue;
            String key    = name.substring("endpoints.".length(), name.length() - ".method".length());
            String method = p.getProperty("endpoints." + key + ".method");
            String relUrl = p.getProperty("endpoints." + key + ".relUrl");
            if (method == null || relUrl == null) {
                throw new IllegalStateException(
                    "Incomplete endpoint '" + key + "' in " + domainName + ".properties");
            }
            out.put(key, new EndpointDefinition(
                HttpVerb.valueOf(method.toUpperCase(Locale.ROOT)), relUrl));
        }
        return Map.copyOf(out);
    }

    /**
     * Loads {@code apiName} from {@code {domainName}.properties}.
     * {@code apiName} is environment-independent — it is a stable display label used for reporting.
     *
     * @param domainName the domain identifier, e.g. {@code "postman-echo"}
     * @return the api name, never null
     * @throws IllegalStateException if the file is missing or apiName is not set
     */
    public static String loadApiName(String domainName) {
        Properties p = loadDomainProperties(domainName);
        return Objects.requireNonNull(
            p.getProperty("apiName"),
            "apiName not set in " + domainName + ".properties");
    }

    private static Properties loadDomainProperties(String domainName) {
        String file = domainName + ".properties";
        Properties props = new Properties();
        try (InputStream in = DomainConfig.class.getClassLoader().getResourceAsStream(file)) {
            if (in == null) {
                throw new IllegalStateException(file + " not found on classpath");
            }
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load " + file, e);
        }
        return props;
    }
}
