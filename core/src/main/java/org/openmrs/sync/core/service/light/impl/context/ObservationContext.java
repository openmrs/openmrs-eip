package org.openmrs.sync.core.service.light.impl.context;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ObservationContext implements Context {

    private String personUuid;

    private String conceptUuid;

    private String conceptClassUuid;

    private String conceptDatatypeUuid;
}
