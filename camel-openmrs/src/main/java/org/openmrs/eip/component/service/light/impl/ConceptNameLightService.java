package org.openmrs.eip.component.service.light.impl;

import org.openmrs.eip.component.entity.light.ConceptNameLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

@Service
public class ConceptNameLightService extends AbstractLightService<ConceptNameLight> {

    public ConceptNameLightService(final OpenmrsRepository<ConceptNameLight> repository) {
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
