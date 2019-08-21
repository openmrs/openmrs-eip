package org.openmrs.sync.component.service.light.impl;

import org.openmrs.sync.component.entity.light.VisitAttributeTypeLight;
import org.openmrs.sync.component.repository.OpenMrsRepository;
import org.openmrs.sync.component.service.light.AbstractAttributeTypeLightService;
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
