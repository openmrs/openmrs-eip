package org.openmrs.sync.component.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Predicate;

public final class DateUtils {

    private DateUtils() {}

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String dateToString(final LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(FORMATTER);
    }

    public static LocalDateTime stringToDate(final String dateAsString) {
        if (dateAsString == null) {
            return null;
        }
        return LocalDateTime.parse(dateAsString, FORMATTER);
    }

    public static boolean isDateAfterAtLeastOneInList(final LocalDateTime date,
                                                      final List<LocalDateTime> dates) {
        if (date == null) {
            return false;
        }
        return dates.stream()
                .map(d -> (Predicate<LocalDateTime>) o -> d != null && date.isAfter(d))
                .reduce(Predicate::or)
                .map(p -> p.test(date))
                .orElse(false);
    }
}
