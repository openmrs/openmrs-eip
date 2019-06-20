package org.openmrs.sync.core.service.light.impl.context;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProgramWorkflowStateContext implements Context {

    private String conceptUuid;

    private String conceptClassUuid;

    private String conceptDatatypeUuid;

    private String workflowUuid;

    private String workflowConceptUuid;

    private String workflowConceptClassUuid;

    private String workflowConceptDatatypeUuid;

    private String workflowProgramUuid;

    private String workflowProgramConceptUuid;

    private String workflowProgramConceptClassUuid;

    private String workflowProgramConceptDatatypeUuid;
}
