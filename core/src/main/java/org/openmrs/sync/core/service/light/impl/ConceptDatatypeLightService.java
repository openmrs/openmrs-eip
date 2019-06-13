package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.ConceptDatatypeLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.AbstractLightServiceNoContext;
import org.springframework.stereotype.Service;

@Service
public class ConceptDatatypeLightService extends AbstractLightServiceNoContext<ConceptDatatypeLight> {

    public ConceptDatatypeLightService(final OpenMrsRepository<ConceptDatatypeLight> repository) {
        super(repository);
    }

    @Override
    protected ConceptDatatypeLight getShadowEntity(final String uuid) {
        ConceptDatatypeLight conceptDataType = new ConceptDatatypeLight();
        conceptDataType.setUuid(uuid);
        conceptDataType.setDateCreated(DEFAULT_DATE);
        conceptDataType.setCreator(DEFAULT_USER_ID);
        conceptDataType.setName(DEFAULT_STRING);
        return conceptDataType;
    }
}
