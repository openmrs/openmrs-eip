package org.openmrs.sync.component.repository;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Repository
public class PatientRepositoryImpl implements PatientRepositoryCustom {

    private PatientRepository patientRepository;

    public PatientRepositoryImpl(@Lazy final PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    /**
     * Transforms result from isPatientInGivenWorkflowStateMySQL into a boolean value as MySQL doesn't support boolean returns
     * @param uuid the uuid of the patient
     * @param workflowStateCode the workflow state code
     * @return boolean
     */
    public boolean isPatientInGivenWorkflowState(final String uuid, final String workflowStateCode) {
        int isPatientInGivenWorkflowState = this.patientRepository.isPatientInGivenWorkflowStateMySQL(uuid, workflowStateCode);

        return isPatientInGivenWorkflowState == 1;
    }
}
