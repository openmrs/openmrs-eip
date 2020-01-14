package org.openmrs.sync.component.service.light.impl;

import org.openmrs.sync.component.entity.light.VisitTypeLight;
import org.openmrs.sync.component.repository.OpenmrsRepository;
import org.openmrs.sync.component.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

@Service
public class VisitTypeLightService extends AbstractLightService<VisitTypeLight> {

    public VisitTypeLightService(final OpenmrsRepository<VisitTypeLight> repository) {
        super(repository);
    }

    @Override
    protected VisitTypeLight createPlaceholderEntity(final String uuid) {
        VisitTypeLight visitType = new VisitTypeLight();
        visitType.setName(DEFAULT_STRING);
        visitType.setCreator(DEFAULT_USER_ID);
        visitType.setDateCreated(DEFAULT_DATE);
        return visitType;
    }
}
