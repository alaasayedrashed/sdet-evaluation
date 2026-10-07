package com.sdet.evaluation.core.testng;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Applies {@link RetryAnalyzer} to every TestNG test, including the Cucumber runner's
 * {@code runScenario} method, so individual tests don't need to declare it.
 *
 * <p>Registered automatically through {@code META-INF/services/org.testng.ITestNGListener}.
 */
public class RetryListener implements IAnnotationTransformer {

    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        annotation.setRetryAnalyzer(RetryAnalyzer.class);
    }
}
