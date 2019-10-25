package org.openmrs.sync.component.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class PatientRepositoryImplTest {

    @Mock
    private PatientRepository patientRepository;

    private PatientRepositoryImpl patientRepositoryImpl;

    private static final String UUID = "uuid";
    private static final String WORKFLOW_STATE_CODE = "code";

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        patientRepositoryImpl = new PatientRepositoryImpl(patientRepository);
    }

    @Test
    public void isPatientInGivenWorkflowState_should_return_true() {
        // Given
        when(patientRepository.isPatientInGivenWorkflowStateMySQL(UUID, WORKFLOW_STATE_CODE)).thenReturn(1);

        // When
        boolean result = patientRepositoryImpl.isPatientInGivenWorkflowState(UUID, WORKFLOW_STATE_CODE);

        // Then
        assertTrue(result);
    }

    @Test
    public void isPatientInGivenWorkflowState_should_return_false() {
        // Given
        when(patientRepository.isPatientInGivenWorkflowStateMySQL(UUID, WORKFLOW_STATE_CODE)).thenReturn(0);

        // When
        boolean result = patientRepositoryImpl.isPatientInGivenWorkflowState(UUID, WORKFLOW_STATE_CODE);

        // Then
        assertFalse(result);
    }
}
