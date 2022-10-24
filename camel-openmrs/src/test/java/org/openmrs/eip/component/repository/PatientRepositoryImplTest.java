package org.openmrs.eip.component.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class PatientRepositoryImplTest {
	
	@Mock
	private PatientRepository patientRepository;
	
	private PatientRepositoryImpl patientRepositoryImpl;
	
	private static final String UUID = "uuid";
	
	private static final String WORKFLOW_STATE_CODES_STRING = "code1;code2";
	
	private static final List<String> WORKFLOW_STATE_CODES = Arrays.asList("code1", "code2");
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		
		patientRepositoryImpl = new PatientRepositoryImpl(patientRepository);
	}
	
	@Test
	public void isPatientInGivenWorkflowState_should_return_true() {
		// Given
		when(patientRepository.isPatientInGivenWorkflowStateMySQL(UUID, WORKFLOW_STATE_CODES)).thenReturn(1);
		
		// When
		boolean result = patientRepositoryImpl.isPatientInGivenWorkflowState(UUID, WORKFLOW_STATE_CODES_STRING);
		
		// Then
		assertTrue(result);
	}
	
	@Test
	public void isPatientInGivenWorkflowState_should_return_false() {
		// Given
		when(patientRepository.isPatientInGivenWorkflowStateMySQL(UUID, WORKFLOW_STATE_CODES)).thenReturn(0);
		
		// When
		boolean result = patientRepositoryImpl.isPatientInGivenWorkflowState(UUID, WORKFLOW_STATE_CODES_STRING);
		
		// Then
		assertFalse(result);
	}
}
