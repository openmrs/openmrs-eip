package org.openmrs.sync.component.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class PatientProgramModel extends BaseChangeableDataModel {

    private String patientUuid;

    private String programUuid;

    private LocalDateTime dateEnrolled;

    private LocalDateTime dateCompleted;

    private String locationUuid;

    private String outcomeConceptUuid;
}
