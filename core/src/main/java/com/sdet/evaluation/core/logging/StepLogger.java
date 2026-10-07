package com.sdet.evaluation.core.logging;

import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

/**
 * Logs a business-level action once and records it in two places: the Log4j2 log
 * (console + per-module file) and the Allure report as a step.
 *
 * <p>Use it for meaningful actions that are not already an {@code @Step}-annotated method,
 * e.g. hook activity or framework events, so logs and report always tell the same story.
 * <pre>{@code
 * private static final StepLogger STEP = StepLogger.forClass(MobileHooks.class);
 * STEP.info("Relaunching app {} after a crash", appPackage);
 * }</pre>
 */
public final class StepLogger {

    private final Logger logger;

    private StepLogger(Logger logger) {
        this.logger = logger;
    }

    /** Creates a step logger that writes under the given class's logger name. */
    public static StepLogger forClass(Class<?> owner) {
        return new StepLogger(LoggerFactory.getLogger(owner));
    }

    /**
     * Logs at INFO and adds a passed Allure step with the same (formatted) message.
     *
     * @param pattern SLF4J-style message pattern using {@code {}} placeholders
     * @param args    placeholder arguments
     */
    public void info(String pattern, Object... args) {
        String message = MessageFormatter.arrayFormat(pattern, args).getMessage();
        logger.info(message);
        Allure.step(message);
    }

    /**
     * Logs at WARN and adds a broken Allure step, without failing the test. Use for
     * recoverable problems such as a screenshot that could not be captured.
     */
    public void warn(String pattern, Object... args) {
        String message = MessageFormatter.arrayFormat(pattern, args).getMessage();
        logger.warn(message);
        Allure.step(message, Status.BROKEN);
    }
}
