package org.openmrs.sync.component.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PatientIdentifierModel extends BaseModel {

    private String patientUuid;

    private String identifier;

    private String patientIdentifierTypeUuid;

    private boolean preferred;

    private String locationUuid;
}
