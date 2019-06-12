package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.ConceptClassLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.attribute.AttributeUuid;
import org.openmrs.sync.core.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConceptClassLightService extends AbstractLightService<ConceptClassLight> {

    public ConceptClassLightService(final OpenMrsRepository<ConceptClassLight> repository) {
        super(repository);
    }

    @Override
    protected ConceptClassLight getFakeEntity(final String uuid, final List<AttributeUuid> uuids) {
        ConceptClassLight conceptClass = new ConceptClassLight();
        conceptClass.setUuid(uuid);
        conceptClass.setDateCreated(DEFAULT_DATE);
        conceptClass.setCreator(DEFAULT_USER_ID);
        conceptClass.setName(DEFAULT_STRING);
        return conceptClass;
    }
}
