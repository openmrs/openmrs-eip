package org.openmrs.sync.core.service.light.impl.context;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PatientProgramContext implements Context {

    private String patientUuid;

    private String programUuid;

    private String programConceptUuid;

    private String programConceptClassUuid;

    private String programConceptDatatypeUuid;
}
