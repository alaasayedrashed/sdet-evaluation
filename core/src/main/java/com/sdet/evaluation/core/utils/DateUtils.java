package com.sdet.evaluation.core.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Optional;

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
     * Today's date formatted with the given pattern, e.g. {@code MM/dd/yyyy -> 10/07/2026}.
     *
     * @param pattern {@link DateTimeFormatter} pattern
     */
    public static String today(String pattern) {
        return format(today(), pattern);
    }

    /** Formats a date with the given pattern using an English locale (stable across machines). */
    public static String format(LocalDate date, String pattern) {
        return date.format(DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH));
    }

    /**
     * Parses an ISO-8601 timestamp such as {@code 2026-10-07T12:30:45.123Z} or
     * {@code 2026-10-07T12:30:45+04:00}.
     *
     * @return the instant, or empty if the text is not a valid ISO-8601 timestamp
     */
    public static Optional<Instant> parseIsoTimestamp(String text) {
        if (text == null || text.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(OffsetDateTime.parse(text).toInstant());
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }

    /** {@code true} when {@link #parseIsoTimestamp(String)} can parse the text. */
    public static boolean isIsoTimestamp(String text) {
        return parseIsoTimestamp(text).isPresent();
    }
}
