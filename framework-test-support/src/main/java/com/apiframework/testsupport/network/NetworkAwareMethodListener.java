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
 * into skipped tests.
 *
 * <p>Replaces the manual {@code try/catch NetworkAwareTestSupport.skipOnNetworkFailure(ex)}
 * pattern in every test method. Registered globally via {@code @Listeners} on
 * {@link com.apiframework.testsupport.base.BaseApiTest}.
 *
 * <p>Behaviour:
 * <ul>
 *     <li>Only intercepts {@code @Test} methods that have FAILED</li>
 *     <li>Walks the cause chain looking for network exceptions</li>
 *     <li>If found: changes status to SKIP with a descriptive message</li>
 *     <li>If not found: leaves the failure untouched</li>
 *     <li>{@link AssertionError} extends {@code Error}, not {@code Exception} —
 *         assertion failures are never network-caused and always propagate normally</li>
 * </ul>
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
