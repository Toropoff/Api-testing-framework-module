package com.apiframework.testsupport.network;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;
import org.testng.SkipException;

import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * TestNG listener that automatically converts network-related test failures
 * into skipped tests. Registered globally via {@code @Listeners} on
 * {@link com.apiframework.testsupport.base.BaseApiTest}.
 *
 * <p>Also provides public static utility methods ({@link #hasNetworkCause},
 * {@link #rootCauseMessage}) used by the retry subsystem.
 */
public final class NetworkAwareMethodListener implements IInvokedMethodListener {

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult result) {
        if (!method.isTestMethod()) {
            return;
        }
        if (result.getStatus() != ITestResult.FAILURE) {
            return;
        }

        Throwable failure = result.getThrowable();
        if (failure != null && hasNetworkCause(failure)) {
            result.setStatus(ITestResult.SKIP);
            result.setThrowable(new SkipException(
                "Skipped (network): " + rootCauseMessage(failure), failure));
        }
    }

    /**
     * Walks the cause chain looking for network connectivity exceptions.
     * Used by the listener and by {@link com.apiframework.testsupport.retry.DefaultRetryPredicate}.
     */
    public static boolean hasNetworkCause(Throwable ex) {
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

    /**
     * Finds the root cause message by walking to the bottom of the cause chain.
     */
    public static String rootCauseMessage(Throwable ex) {
        Throwable root = ex;
        while (root.getCause() != null) {
            root = root.getCause();
        }
        return root.getMessage();
    }
}
