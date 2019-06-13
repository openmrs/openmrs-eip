package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.ConceptClassLight;
import org.openmrs.sync.core.service.light.AbstractLightServiceNoContext;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.springframework.stereotype.Service;

@Service
public class ConceptClassLightService extends AbstractLightServiceNoContext<ConceptClassLight> {

    public ConceptClassLightService(final OpenMrsRepository<ConceptClassLight> repository) {
        super(repository);
    }

    @Override
    protected ConceptClassLight getShadowEntity(final String uuid) {
        ConceptClassLight conceptClass = new ConceptClassLight();
        conceptClass.setUuid(uuid);
        conceptClass.setDateCreated(DEFAULT_DATE);
        conceptClass.setCreator(DEFAULT_USER_ID);
        conceptClass.setName(DEFAULT_STRING);
        return conceptClass;
    }
}
