package org.openmrs.sync.core.service.light.impl.context;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderContext implements Context {

    private String orderTypeUuid;

    private String conceptUuid;

    private String conceptClassUuid;

    private String conceptDatatypeUuid;

    private String providerUuid;

    private String encounterUuid;

    private String encounterPatientUuid;

    private String encounterEncounterTypeUuid;

    private String patientUuid;

    private String careSettingUuid;
}
