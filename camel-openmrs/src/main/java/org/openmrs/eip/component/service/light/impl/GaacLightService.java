package org.openmrs.eip.component.service.light.impl;

import org.openmrs.eip.component.entity.light.GaacLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

@Service
public class GaacLightService extends AbstractLightService<GaacLight> {
    public GaacLightService(final OpenmrsRepository<GaacLight> repository) {
        super(repository);
    }

    @Override
    protected GaacLight createPlaceholderEntity(final String uuid) {
    	GaacLight gaac = new GaacLight();
        gaac.setDateCreated(DEFAULT_DATE);
        gaac.setCreator(DEFAULT_USER_ID);
        gaac.setName(DEFAULT_STRING);
        gaac.setStartDate(DEFAULT_DATE);
        return gaac;
    }
}
