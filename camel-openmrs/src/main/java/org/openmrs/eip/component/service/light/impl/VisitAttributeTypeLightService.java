package org.openmrs.eip.component.service.light.impl;

import org.openmrs.eip.component.entity.light.VisitAttributeTypeLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.service.light.AbstractAttributeTypeLightService;
import org.springframework.stereotype.Service;

@Service
public class VisitAttributeTypeLightService extends AbstractAttributeTypeLightService<VisitAttributeTypeLight> {

    public VisitAttributeTypeLightService(final OpenmrsRepository<VisitAttributeTypeLight> repository) {
        super(repository);
    }

    @Override
    protected VisitAttributeTypeLight createEntity() {
        return new VisitAttributeTypeLight();
    }
}
