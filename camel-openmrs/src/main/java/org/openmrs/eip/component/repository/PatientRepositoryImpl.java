package org.openmrs.eip.component.repository;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public class PatientRepositoryImpl implements PatientRepositoryCustom {

    private PatientRepository patientRepository;

    public PatientRepositoryImpl(@Lazy final PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public boolean isPatientInGivenWorkflowState(final String uuid, final String workflowStateConceptMappingsString) {

        List<String> workflowStateConceptMappings = parseMappings(workflowStateConceptMappingsString);

        int isPatientInGivenWorkflowState = this.patientRepository.isPatientInGivenWorkflowStateMySQL(uuid, workflowStateConceptMappings);

        return isPatientInGivenWorkflowState == 1;
    }

    private List<String> parseMappings(final String workflowStateConceptMappingsString) {
        return Arrays.asList(workflowStateConceptMappingsString.split(";"));
    }
}
