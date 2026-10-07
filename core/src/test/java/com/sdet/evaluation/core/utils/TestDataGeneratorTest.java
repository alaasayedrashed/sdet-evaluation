package com.sdet.evaluation.core.utils;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestDataGeneratorTest {

    @Test
    public void generatesUniqueValuesWithPrefix() {
        String first = TestDataGenerator.uniqueValue("user");
        String second = TestDataGenerator.uniqueValue("user");

        assertThat(first).as("prefix and suffix format").matches("user_[a-z0-9]{6}");
        assertThat(first).as("two generated values must differ").isNotEqualTo(second);
    }

    @Test
    public void generatesLowercaseEmails() {
        assertThat(TestDataGenerator.uniqueEmail("QA", "Example.com")).matches("qa_[a-z0-9]{6}@example\\.com");
    }
}
