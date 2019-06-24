package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.VisitAttributeTypeLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.AbstractAttributeTypeLightService;
import org.springframework.stereotype.Service;

@Service
public class VisitAttributeTypeLightService extends AbstractAttributeTypeLightService<VisitAttributeTypeLight> {

    public VisitAttributeTypeLightService(final OpenMrsRepository<VisitAttributeTypeLight> repository) {
        super(repository);
    }

    @Override
    protected VisitAttributeTypeLight createEntity() {
        return new VisitAttributeTypeLight();
    }
}
