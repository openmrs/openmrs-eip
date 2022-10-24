package org.openmrs.eip.component.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ObservationRepositoryImplTest {
	
	@Mock
	private ObservationRepository observationRepository;
	
	private ObservationRepositoryImpl observationRepositoryImpl;
	
	private static final String UUID = "uuid";
	
	private static final String CONCEPT_MAPPING = "code1";
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		
		observationRepositoryImpl = new ObservationRepositoryImpl(observationRepository);
	}
	
	@Test
	public void isPatientInGivenWorkflowState_should_return_true() {
		// Given
		when(observationRepository.isObsLinkedToGivenConceptMappingMySQL(UUID, CONCEPT_MAPPING)).thenReturn(1);
		
		// When
		boolean result = observationRepositoryImpl.isObsLinkedToGivenConceptMapping(UUID, CONCEPT_MAPPING);
		
		// Then
		assertTrue(result);
	}
	
	@Test
	public void isPatientInGivenWorkflowState_should_return_false() {
		// Given
		when(observationRepository.isObsLinkedToGivenConceptMappingMySQL(UUID, CONCEPT_MAPPING)).thenReturn(0);
		
		// When
		boolean result = observationRepositoryImpl.isObsLinkedToGivenConceptMapping(UUID, CONCEPT_MAPPING);
		
		// Then
		assertFalse(result);
	}
}
