package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.ConceptDatatypeLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.attribute.AttributeUuid;
import org.openmrs.sync.core.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConceptDatatypeLightService extends AbstractLightService<ConceptDatatypeLight> {

    public ConceptDatatypeLightService(final OpenMrsRepository<ConceptDatatypeLight> repository) {
        super(repository);
    }

    @Override
    protected ConceptDatatypeLight getFakeEntity(final String uuid, final List<AttributeUuid> uuids) {
        ConceptDatatypeLight conceptDataType = new ConceptDatatypeLight();
        conceptDataType.setUuid(uuid);
        conceptDataType.setDateCreated(DEFAULT_DATE);
        conceptDataType.setCreator(DEFAULT_USER_ID);
        conceptDataType.setName(DEFAULT_STRING);
        return conceptDataType;
    }
}
