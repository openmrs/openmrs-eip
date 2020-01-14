package org.openmrs.sync.component.service.light.impl;

import org.openmrs.sync.component.entity.light.ConceptAttributeTypeLight;
import org.openmrs.sync.component.repository.OpenmrsRepository;
import org.openmrs.sync.component.service.light.AbstractAttributeTypeLightService;
import org.springframework.stereotype.Service;

@Service
public class ConceptAttributeTypeLightService extends AbstractAttributeTypeLightService<ConceptAttributeTypeLight> {

    public ConceptAttributeTypeLightService(final OpenmrsRepository<ConceptAttributeTypeLight> repository) {
        super(repository);
    }

    @Override
    protected ConceptAttributeTypeLight createEntity() {
        return new ConceptAttributeTypeLight();
    }
}
