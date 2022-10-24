package org.openmrs.eip.component.entity;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ObservationTest {
	
	private static final LocalDateTime DATE1 = LocalDateTime.of(2019, 5, 1, 0, 0);
	
	private static final LocalDateTime DATE2 = LocalDateTime.of(2019, 6, 1, 0, 0);
	
	@Test
	public void date_created_to_test_before_date_created_in_param() {
		// Given
		Observation entityToTest = new Observation();
		entityToTest.setDateCreated(DATE1);
		Observation entityInParam = new Observation();
		entityInParam.setDateCreated(DATE2);
		
		// When
		boolean result = entityToTest.wasModifiedAfter(entityInParam);
		
		// Then
		assertFalse(result);
	}
	
	@Test
	public void date_voided_to_test_before_date_created_in_param() {
		// Given
		Observation entityToTest = new Observation();
		entityToTest.setDateCreated(DATE1);
		Observation entityInParam = new Observation();
		entityInParam.setDateVoided(DATE2);
		
		// When
		boolean result = entityToTest.wasModifiedAfter(entityInParam);
		
		// Then
		assertFalse(result);
	}
	
	@Test
	public void date_created_to_test_before_date_voided_in_param() {
		// Given
		Observation entityToTest = new Observation();
		entityToTest.setDateCreated(DATE1);
		Observation entityInParam = new Observation();
		entityInParam.setDateVoided(DATE2);
		
		// When
		boolean result = entityToTest.wasModifiedAfter(entityInParam);
		
		// Then
		assertFalse(result);
	}
	
	@Test
	public void date_voided_to_test_before_date_voided_in_param() {
		// Given
		Observation entityToTest = new Observation();
		entityToTest.setDateVoided(DATE1);
		Observation entityInParam = new Observation();
		entityInParam.setDateVoided(DATE2);
		
		// When
		boolean result = entityToTest.wasModifiedAfter(entityInParam);
		
		// Then
		assertFalse(result);
	}
	
	@Test
	public void date_voided_to_test_after_date_created_in_param() {
		// Given
		Observation entityToTest = new Observation();
		entityToTest.setDateVoided(DATE2);
		Observation entityInParam = new Observation();
		entityInParam.setDateCreated(DATE1);
		
		// When
		boolean result = entityToTest.wasModifiedAfter(entityInParam);
		
		// Then
		assertTrue(result);
	}
	
	@Test
	public void date_voided_to_test_after_date_voided_in_param() {
		// Given
		Observation entityToTest = new Observation();
		entityToTest.setDateVoided(DATE2);
		Observation entityInParam = new Observation();
		entityInParam.setDateVoided(DATE1);
		
		// When
		boolean result = entityToTest.wasModifiedAfter(entityInParam);
		
		// Then
		assertTrue(result);
	}
}
