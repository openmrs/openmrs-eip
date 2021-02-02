package org.openmrs.eip.component.service.light.impl;

import org.openmrs.eip.component.entity.light.GaacReasonLeavingLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

@Service
public class GaacReasonLeavingLightService extends AbstractLightService<GaacReasonLeavingLight> {

    public GaacReasonLeavingLightService(final OpenmrsRepository<GaacReasonLeavingLight> repository) {
        super(repository);
    }

    @Override
    protected GaacReasonLeavingLight createPlaceholderEntity(final String uuid) {
    	GaacReasonLeavingLight reasonLeaving = new GaacReasonLeavingLight();
        reasonLeaving.setName(DEFAULT_STRING);
        reasonLeaving.setCreator(DEFAULT_USER_ID);
        reasonLeaving.setDateCreated(DEFAULT_DATE);
        return reasonLeaving;
    }
}
