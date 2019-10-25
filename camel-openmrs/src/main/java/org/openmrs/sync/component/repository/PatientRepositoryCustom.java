package org.openmrs.sync.component.repository;

public interface PatientRepositoryCustom {

    boolean isPatientInGivenWorkflowState(final String uuid, final String workflowStateCode);
}
