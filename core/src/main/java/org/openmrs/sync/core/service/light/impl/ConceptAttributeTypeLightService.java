package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.ConceptAttributeTypeLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.AbstractAttributeTypeLightService;
import org.springframework.stereotype.Service;

@Service
public class ConceptAttributeTypeLightService extends AbstractAttributeTypeLightService<ConceptAttributeTypeLight> {

    public ConceptAttributeTypeLightService(final OpenMrsRepository<ConceptAttributeTypeLight> repository) {
        super(repository);
    }

    @Override
    protected ConceptAttributeTypeLight createEntity() {
        return new ConceptAttributeTypeLight();
    }
}
