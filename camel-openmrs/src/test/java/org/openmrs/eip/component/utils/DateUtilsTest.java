package org.openmrs.eip.component.utils;

import static java.time.LocalDateTime.of;
import static java.time.Month.AUGUST;
import static java.time.ZoneId.systemDefault;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.component.utils.DateUtils.containsLatestDate;
import static org.openmrs.eip.component.utils.DateUtils.isDateAfterOrEqual;
import static org.openmrs.eip.component.utils.DateUtils.stringToDate;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.junit.Test;

public class DateUtilsTest {
	
	private static final int YY = 2020;
	
	private static final int MM = 12;
	
	private static final int DD = 3;
	
	private static final int H = 12;
	
	private static final int M = 12;
	
	private static final int S = 12;
	
	@Test
	public void dateToString_should_return_date_as_string() {
		// Given
		LocalDateTime date = of(2019, 6, 5, 16, 59, 22);
		
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
		assertEquals(of(2019, 6, 5, 16, 59, 22), result);
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
	public void containsLatestDate_shouldReturnFalseIfCollection1ContainsNullValuesOnly() {
		List<LocalDateTime> dates = asList(null, null);
		assertFalse(containsLatestDate(dates, dates));
		assertFalse(containsLatestDate(dates, asList(of(YY, MM, DD, H, M, S))));
	}
	
	@Test
	public void containsLatestDate_shouldReturnFalseIfCollection1DoesNotContainTheLatestDate() {
		//matching latest dates with some none null values
		List<LocalDateTime> dates1 = asList(null, of(YY, MM, DD, H, M, S));
		List<LocalDateTime> dates2 = asList(of(YY, MM, DD, H, M, S), null);
		assertFalse(containsLatestDate(dates1, dates2));
		
		//second list contains the latest
		dates2 = asList(of(YY, MM, DD, H, M, S + 1), null);
		assertFalse(containsLatestDate(dates1, dates2));
		
		//no nulls where with all dates match
		dates1 = asList(of(YY, MM, DD, H, M, S), of(YY, MM, DD, H, M, S));
		dates2 = asList(of(YY, MM, DD, H, M, S), of(YY, MM, DD, H, M, S));
		assertFalse(containsLatestDate(dates1, dates2));
		
		//no nulls where second list contains the latest
		dates2 = asList(of(YY, MM, DD, H, M, S), of(YY, MM, DD, H, M, S + 1));
		assertFalse(containsLatestDate(dates1, dates2));
		
		//no nulls where second list contains the earliest and latest
		dates2 = asList(of(YY, MM, DD, H, M, S), of(YY, MM, DD, H, M, S + 1), of(YY, MM, DD, H, M, S - 1));
		assertFalse(containsLatestDate(dates1, dates2));
	}
	
	@Test
	public void containsLatestDate_shouldReturnTrueIfCollection1ContainsTheLatestDate() {
		List<LocalDateTime> dates1 = asList(null, of(YY, MM, DD, H, M, S));
		List<LocalDateTime> dates2 = asList(null, null);
		assertTrue(containsLatestDate(dates1, dates2));
		
		dates1 = asList(of(YY, MM, DD, H, M, S), of(YY, MM, DD, H, M, S + 1));
		dates2 = asList(of(YY, MM, DD, H, M, S), of(YY, MM, DD, H, M, S));
		assertTrue(containsLatestDate(dates1, dates2));
		
		//first list contains the earliest and latest
		dates1 = asList(of(YY, MM, DD, H, M, S), of(YY, MM, DD, H, M, S + 1), of(YY, MM, DD, H, M, S - 1));
		assertTrue(containsLatestDate(dates1, dates2));
	}
	
	@Test
	public void subtractDays_shouldReturnTheResultDate() {
		Date asOfDate = Date.from(stringToDate("2023-01-02 15:33:27").atZone(systemDefault()).toInstant());
		Date expectedDate = Date.from(stringToDate("2023-01-01 15:33:27").atZone(systemDefault()).toInstant());
		assertEquals(expectedDate, DateUtils.subtractDays(asOfDate, 1));
		
		expectedDate = Date.from(stringToDate("2022-12-31 15:33:27").atZone(systemDefault()).toInstant());
		assertEquals(expectedDate, DateUtils.subtractDays(asOfDate, 2));
	}
	
	@Test
	public void isDateAfterOrEqual_shouldReturnTrueIfBothDatesAreNull() {
		assertTrue(isDateAfterOrEqual(null, null));
	}
	
	@Test
	public void isDateAfterOrEqual_shouldReturnTrueIfDateIsNotNullOtherIsNull() {
		assertTrue(isDateAfterOrEqual(of(2023, AUGUST, 18, 00, 00, 00), null));
	}
	
	@Test
	public void isDateAfterOrEqual_shouldReturnFalseIfDateIsNullAndOtherIsNotNull() {
		assertFalse(isDateAfterOrEqual(null, of(2023, AUGUST, 18, 00, 00, 00)));
	}
	
	@Test
	public void isDateAfterOrEqual_shouldReturnTrueIfDateIsAfterOther() {
		assertTrue(isDateAfterOrEqual(of(2023, AUGUST, 18, 00, 00, 01), of(2023, AUGUST, 18, 00, 00, 00)));
	}
	
	@Test
	public void isDateAfterOrEqual_shouldReturnFalseIfDateIsBeforeOther() {
		assertFalse(isDateAfterOrEqual(of(2023, AUGUST, 18, 00, 00, 00), of(2023, AUGUST, 18, 00, 00, 01)));
	}
	
	@Test
	public void isDateAfterOrEqual_shouldReturnTrueIfTheDatesMatch() {
		assertTrue(isDateAfterOrEqual(of(2023, AUGUST, 18, 00, 00, 00), of(2023, AUGUST, 18, 00, 00, 00)));
	}
	
}
