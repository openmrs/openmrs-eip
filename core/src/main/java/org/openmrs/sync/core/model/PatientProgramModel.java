package org.openmrs.sync.core.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class PatientProgramModel extends BaseModel {

    private String patientUuid;

    private String programUuid;

    private String programConceptUuid;

    private String programConceptClassUuid;

    private String programConceptDatatypeUuid;

    private LocalDateTime dateEnrolled;

    private LocalDateTime dateCompleted;

    private String locationUuid;

    private String outcomeConceptUuid;

    private String outcomeConceptClassUuid;

    private String outcomeConceptDatatypeUuid;
}
