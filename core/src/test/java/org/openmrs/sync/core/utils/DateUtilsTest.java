package org.openmrs.sync.core.utils;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

public class DateUtilsTest {

    @Test
    public void dateToString() {
        // Given
        LocalDateTime date = LocalDateTime.of(2019, 6, 5, 16, 59);

        // When
        String result = DateUtils.dateToString(date);

        // Then
        assertEquals("2019-06-05-1659", result);
    }

    @Test
    public void stringToDate() {
        // Given
        String dateAsString = "2019-06-05-1659";

        // When
        LocalDateTime result = DateUtils.stringToDate(dateAsString);

        // Then
        assertEquals(LocalDateTime.of(2019, 6, 5, 16, 59), result);
    }
}
