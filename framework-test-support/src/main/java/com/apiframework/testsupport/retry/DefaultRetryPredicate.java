package com.apiframework.testsupport.retry;

import com.apiframework.testsupport.network.NetworkAwareMethodListener;
import org.testng.ITestResult;

/**
 * Default retry predicate: retries on network failures and transport errors.
 * Delegates network detection to {@link NetworkAwareMethodListener#hasNetworkCause}
 * to avoid duplicating the exception-type check.
 */
public final class DefaultRetryPredicate implements RetryPredicate {
    @Override
    public boolean shouldRetry(ITestResult result, Throwable failure) {
        if (failure == null) {
            return false;
        }
        if (failure instanceof AssertionError) {
            return false;
        }
        if (NetworkAwareMethodListener.hasNetworkCause(failure)) {
            return true;
        }

        String message = failure.getMessage() == null ? "" : failure.getMessage().toLowerCase();
        return message.contains("timeout") || message.contains("connection reset")
            || message.contains("502") || message.contains("503") || message.contains("504");
    }
}
