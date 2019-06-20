package org.openmrs.sync.core.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class PatientStateModel extends BaseModel {

    private String patientProgramUuid;

    private String patientProgramProgramUuid;

    private String patientProgramProgramConceptUuid;

    private String patientProgramProgramConceptClassUuid;

    private String patientProgramProgramConceptDatatypeUuid;

    private String patientProgramPatientUuid;

    private String stateUuid;

    private String stateConceptUuid;

    private String stateConceptClassUuid;

    private String stateConceptDatatypeUuid;

    private String stateWorkflowUuid;

    private String stateWorkflowConceptUuid;

    private String stateWorkflowConceptClassUuid;

    private String stateWorkflowConceptDatatypeUuid;

    private String stateWorkflowProgramUuid;

    private String stateWorkflowProgramConceptUuid;

    private String stateWorkflowProgramConceptClassUuid;

    private String stateWorkflowProgramConceptDatatypeUuid;

    private LocalDate startDate;

    private LocalDate endDate;
}
