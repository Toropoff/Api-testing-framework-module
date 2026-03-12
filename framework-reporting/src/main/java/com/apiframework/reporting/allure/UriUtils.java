package com.apiframework.reporting.allure;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * URI helpers shared by Allure reporting strategies.
 */
final class UriUtils {

    private UriUtils() {
    }

    /**
     * Extracts the path component from a full URI string.
     * Returns the raw URI on malformed input so report attachments stay useful.
     */
    static String pathFromUri(String uri) {
        try {
            String path = new URI(uri).getPath();
            return (path == null || path.isBlank()) ? "/" : path;
        } catch (URISyntaxException ignored) {
            return uri;
        }
    }
}
