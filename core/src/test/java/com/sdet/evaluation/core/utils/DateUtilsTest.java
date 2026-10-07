package com.sdet.evaluation.core.utils;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class DateUtilsTest {

    @Test
    public void formatsDatesWithPattern() {
        assertThat(DateUtils.format(LocalDate.of(2026, 3, 9), "MM/dd/yyyy")).isEqualTo("03/09/2026");
    }

    @Test
    public void todayMatchesSystemClock() {
        assertThat(DateUtils.today("yyyy-MM-dd")).isEqualTo(LocalDate.now().toString());
    }

    @DataProvider
    public Object[][] timestamps() {
        return new Object[][]{
                {"2026-10-07T12:30:45.123Z", true},
                {"2026-10-07T12:30:45+04:00", true},
                {"2026-10-07", false},
                {"not-a-date", false},
                {"", false},
                {null, false}
        };
    }

    @Test(dataProvider = "timestamps")
    public void recognisesIsoTimestamps(String text, boolean expected) {
        assertThat(DateUtils.isIsoTimestamp(text)).as("is '%s' an ISO timestamp", text).isEqualTo(expected);
    }
}
