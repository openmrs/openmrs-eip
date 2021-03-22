package org.openmrs.eip.component.repository;

public interface PatientRepositoryCustom {

    /**
     * Transforms result from isPatientInGivenWorkflowStateMySQL into a boolean value as MySQL doesn't support boolean returns
     * @param uuid the uuid of the patient
     * @param workflowStateConceptMappingsString list of workflow state codes as string separated by a semicolon
     * @return boolean
     */
    boolean isPatientInGivenWorkflowState(final String uuid, final String workflowStateConceptMappingsString);
}
