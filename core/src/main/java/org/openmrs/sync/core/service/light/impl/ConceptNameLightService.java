package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.ConceptNameLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

@Service
public class ConceptNameLightService extends AbstractLightService<ConceptNameLight> {

    public ConceptNameLightService(final OpenMrsRepository<ConceptNameLight> repository) {
        super(repository);
    }

    @Override
    protected ConceptNameLight createPlaceholderEntity(final String uuid) {
        ConceptNameLight conceptName = new ConceptNameLight();
        conceptName.setDateCreated(DEFAULT_DATE);
        conceptName.setCreator(DEFAULT_USER_ID);
        conceptName.setLocale("en");
        conceptName.setName(DEFAULT_STRING);
        return conceptName;
    }
}
