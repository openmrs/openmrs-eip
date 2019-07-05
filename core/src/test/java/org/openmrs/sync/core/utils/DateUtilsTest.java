package org.openmrs.sync.core.utils;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class DateUtilsTest {

    @Test
    public void dateToString_should_return_date_as_string() {
        // Given
        LocalDateTime date = LocalDateTime.of(2019, 6, 5, 16, 59, 22);

        // When
        String result = DateUtils.dateToString(date);

        // Then
        assertEquals("2019-06-05 16:59:22", result);
    }

    @Test
    public void dateToString_should_return_null() {
        // Given
        LocalDateTime date = null;

        // When
        String result = DateUtils.dateToString(date);

        // Then
        assertNull(result);
    }

    @Test
    public void stringToDate_should_return_date() {
        // Given
        String dateAsString = "2019-06-05 16:59:22";

        // When
        LocalDateTime result = DateUtils.stringToDate(dateAsString);

        // Then
        assertEquals(LocalDateTime.of(2019, 6, 5, 16, 59, 22), result);
    }

    @Test
    public void stringToDate_should_return_null() {
        // Given
        String dateAsString = null;

        // When
        LocalDateTime result = DateUtils.stringToDate(dateAsString);

        // Then
        assertNull(result);
    }

    @Test
    public void isDateAfterAtLeastOneInList_should_return_true() {
        // Given
        LocalDateTime dateToTest = LocalDateTime.of(2019, 6, 10, 0, 0);
        LocalDateTime date1 = LocalDateTime.of(2016, 6, 10, 0, 0);
        LocalDateTime date2 = LocalDateTime.of(2020, 6, 10, 0, 0);
        LocalDateTime date3 = LocalDateTime.of(2021, 6, 10, 0, 0);
        List<LocalDateTime> dates = Arrays.asList(date1, date2, date3);

        // When
        boolean result = DateUtils.isDateAfterAtLeastOneInList(dateToTest, dates);

        // Then
        assertTrue(result);
    }

    @Test
    public void isDateAfterAtLeastOneInList_should_return_false() {
        // Given
        LocalDateTime dateToTest = LocalDateTime.of(2019, 6, 10, 0, 0);
        LocalDateTime date1 = LocalDateTime.of(2020, 6, 10, 0, 0);
        LocalDateTime date2 = LocalDateTime.of(2021, 6, 10, 0, 0);
        LocalDateTime date3 = LocalDateTime.of(2022, 6, 10, 0, 0);
        List<LocalDateTime> dates = Arrays.asList(date1, date2, date3);

        // When
        boolean result = DateUtils.isDateAfterAtLeastOneInList(dateToTest, dates);

        // Then
        assertFalse(result);
    }

    @Test
    public void isDateAfterAtLeastOneInList_should_return_false_if_all_null() {
        // Given
        LocalDateTime dateToTest = LocalDateTime.of(2019, 6, 10, 0, 0);
        List<LocalDateTime> dates = Arrays.asList(null, null, null);

        // When
        boolean result = DateUtils.isDateAfterAtLeastOneInList(dateToTest, dates);

        // Then
        assertFalse(result);
    }
}
