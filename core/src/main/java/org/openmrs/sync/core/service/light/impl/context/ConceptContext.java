package org.openmrs.sync.core.service.light.impl.context;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConceptContext implements Context {

    private String conceptClassUuid;

    private String conceptDatatypeUuid;
}
