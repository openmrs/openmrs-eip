package org.openmrs.sync.core.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateUtils {

    private DateUtils() {}

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmm");

    public static String toString(final LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(FORMATTER);
    }

    public static LocalDateTime fromString(final String dateAsString) {
        if (dateAsString == null) {
            return null;
        }
        return LocalDateTime.parse(dateAsString, FORMATTER);
    }
}
