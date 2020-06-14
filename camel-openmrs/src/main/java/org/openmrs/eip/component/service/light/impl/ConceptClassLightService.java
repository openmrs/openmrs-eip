package org.openmrs.eip.component.service.light.impl;

import org.openmrs.eip.component.entity.light.ConceptClassLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

@Service
public class ConceptClassLightService extends AbstractLightService<ConceptClassLight> {

    public ConceptClassLightService(final OpenmrsRepository<ConceptClassLight> repository) {
        super(repository);
    }

    @Override
    protected ConceptClassLight createPlaceholderEntity(final String uuid) {
        ConceptClassLight conceptClass = new ConceptClassLight();
        conceptClass.setDateCreated(DEFAULT_DATE);
        conceptClass.setCreator(DEFAULT_USER_ID);
        conceptClass.setName(DEFAULT_STRING);
        return conceptClass;
    }
}
