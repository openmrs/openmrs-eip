package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.ConceptNameLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.AbstractLightServiceNoContext;
import org.springframework.stereotype.Service;

@Service
public class ConceptNameLightService extends AbstractLightServiceNoContext<ConceptNameLight> {

    public ConceptNameLightService(final OpenMrsRepository<ConceptNameLight> repository) {
        super(repository);
    }

    @Override
    protected ConceptNameLight getShadowEntity(final String uuid) {
        ConceptNameLight conceptName = new ConceptNameLight();
        conceptName.setUuid(uuid);
        conceptName.setDateCreated(DEFAULT_DATE);
        conceptName.setCreator(DEFAULT_USER_ID);
        conceptName.setLocale("en");
        conceptName.setName(DEFAULT_STRING);
        return conceptName;
    }
}
