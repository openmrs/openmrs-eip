package org.openmrs.sync.component.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class PatientStateModel extends BaseModel {

    private String patientProgramUuid;

    private String stateUuid;

    private LocalDate startDate;

    private LocalDate endDate;
}
