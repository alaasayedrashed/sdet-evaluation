package com.sdet.evaluation.core.testng;

import com.sdet.evaluation.core.config.FrameworkConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Flakiness strategy: re-runs a failed test up to {@code retry.count} times (default 0).
 *
 * <p>Retries hide real defects if overused, so the default is off and the value is meant to be
 * raised only for environments with known infrastructure noise, e.g. {@code -Dretry.count=1}
 * on a CI emulator. Previous attempts stay visible in Allure under "Retries".
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger LOG = LoggerFactory.getLogger(RetryAnalyzer.class);

    private final int maxRetries = FrameworkConfig.retryCount();
    private int attempts;

    @Override
    public boolean retry(ITestResult result) {
        if (attempts < maxRetries) {
            attempts++;
            LOG.warn("Retrying '{}' (attempt {} of {}) after failure: {}",
                    result.getName(), attempts, maxRetries,
                    result.getThrowable() == null ? "n/a" : result.getThrowable().getMessage());
            return true;
        }
        return false;
    }
}
