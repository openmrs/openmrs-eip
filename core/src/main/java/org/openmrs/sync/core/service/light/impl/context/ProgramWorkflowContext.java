package org.openmrs.sync.core.service.light.impl.context;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProgramWorkflowContext implements Context {

    private String conceptUuid;

    private String conceptClassUuid;

    private String conceptDatatypeUuid;

    private String programUuid;

    private String programConceptUuid;

    private String programConceptClassUuid;

    private String programConceptDatatypeUuid;
}
