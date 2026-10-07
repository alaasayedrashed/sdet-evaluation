package com.sdet.evaluation.core.config;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ConfigReaderTest {

    private static final String OVERRIDDEN_KEY = "sample.override.me";

    private ConfigReader config;

    @BeforeClass
    public void loadConfig() {
        config = ConfigReader.fromClasspath("config/core-test.properties");
    }

    @AfterMethod(alwaysRun = true)
    public void clearOverrides() {
        System.clearProperty(OVERRIDDEN_KEY);
    }

    @Test
    public void readsValuesFromFile() {
        assertThat(config.get("sample.url")).as("string property").isEqualTo("https://example.com");
        assertThat(config.getInt("sample.count")).as("int property").isEqualTo(3);
        assertThat(config.getBoolean("sample.enabled")).as("case-insensitive boolean").isTrue();
    }

    @Test
    public void systemPropertyOverridesFile() {
        System.setProperty(OVERRIDDEN_KEY, "from-system");

        assertThat(config.get(OVERRIDDEN_KEY)).as("-D override must win over the file").isEqualTo("from-system");
    }

    @Test
    public void blankValuesFallBackToDefaults() {
        assertThat(config.find("sample.blank")).as("blank value is treated as undefined").isEmpty();
        assertThat(config.get("sample.blank", "fallback")).isEqualTo("fallback");
        assertThat(config.getInt("sample.missing", 42)).isEqualTo(42);
        assertThat(config.getBoolean("sample.missing", true)).isTrue();
    }

    @Test
    public void parsesDurationsWithUnits() {
        assertThat(config.getDuration("sample.timeout")).isEqualTo(Duration.ofSeconds(15));
        assertThat(config.getDuration("sample.timeout.millis")).isEqualTo(Duration.ofMillis(500));
        assertThat(config.getDuration("sample.timeout.minutes")).isEqualTo(Duration.ofMinutes(2));
        assertThat(config.getDuration("sample.timeout.bare")).as("bare number means seconds").isEqualTo(Duration.ofSeconds(7));
    }

    @Test
    public void failsFastWithHelpfulMessages() {
        assertThatThrownBy(() -> config.get("sample.missing"))
                .isInstanceOf(ConfigurationException.class)
                .hasMessageContaining("sample.missing")
                .hasMessageContaining("SAMPLE_MISSING");
        assertThatThrownBy(() -> config.getDuration("sample.timeout.invalid"))
                .isInstanceOf(ConfigurationException.class)
                .hasMessageContaining("fast");
        assertThatThrownBy(() -> config.getInt("sample.url"))
                .isInstanceOf(ConfigurationException.class);
        assertThatThrownBy(() -> ConfigReader.fromClasspath("config/does-not-exist.properties"))
                .isInstanceOf(ConfigurationException.class)
                .hasMessageContaining("does-not-exist");
    }

    @Test
    public void derivesEnvironmentVariableNames() {
        assertThat(ConfigReader.toEnvironmentKey("web.base-url")).isEqualTo("WEB_BASE_URL");
    }
}
