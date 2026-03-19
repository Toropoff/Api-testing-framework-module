package com.apiframework.testsupport.network;

import org.testng.SkipException;

import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Utility for handling network-related test failures.
 *
 * <p>Usage in test methods:
 * <pre>{@code
 * try {
 *     // test logic — assertion errors bypass this catch entirely
 * } catch (Exception ex) {
 *     NetworkAwareTestSupport.skipOnNetworkFailure(ex);
 * }
 * }</pre>
 *
 * <p>Behaviour:
 * <ul>
 *     <li>Network exceptions (wrapped at any depth) → {@link SkipException} with full cause chain</li>
 *     <li>All other exceptions (NPE, IllegalState, etc.) → rethrown as-is to fail the test</li>
 *     <li>{@link AssertionError} extends {@code Error}, not {@code Exception},
 *         so assertion failures are never caught and always propagate with full stacktrace</li>
 * </ul>
 */
public final class NetworkAwareTestSupport {

    private NetworkAwareTestSupport() {
    }

    /**
     * If the exception (or any cause in its chain) is a network connectivity issue,
     * throws a {@link SkipException}. Otherwise, rethrows the original exception.
     */
    public static void skipOnNetworkFailure(Exception ex) {
        if (hasNetworkCause(ex)) {
            throw new SkipException("Skipped (network): " + rootCauseMessage(ex), ex);
        }
        if (ex instanceof RuntimeException runtimeException) {
            throw runtimeException;
        }
        throw new RuntimeException(ex);
    }

    private static boolean hasNetworkCause(Throwable ex) {
        Throwable current = ex;
        while (current != null) {
            if (current instanceof ConnectException
                || current instanceof SocketTimeoutException
                || current instanceof UnknownHostException
                || current instanceof NoRouteToHostException) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private static String rootCauseMessage(Throwable ex) {
        Throwable root = ex;
        while (root.getCause() != null) {
            root = root.getCause();
        }
        return root.getMessage();
    }
}
