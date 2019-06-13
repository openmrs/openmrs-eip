package org.openmrs.sync.core.service.light.impl.context;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VisitContext implements Context {

    private String patientUuid;

    private String visitTypeUuid;
}
