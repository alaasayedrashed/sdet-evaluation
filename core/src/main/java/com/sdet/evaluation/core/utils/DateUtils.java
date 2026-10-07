package com.sdet.evaluation.core.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Date helpers so tests never hardcode dates.
 */
public final class DateUtils {

    private DateUtils() {
    }

    /** Today's date in the system time zone. */
    public static LocalDate today() {
        return LocalDate.now(ZoneId.systemDefault());
    }

    /**
     * Formats a date with the given pattern using an English locale (stable across machines),
     * e.g. {@code MM/dd/yyyy -> 10/07/2026}.
     */
    public static String format(LocalDate date, String pattern) {
        return date.format(DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH));
    }
}
