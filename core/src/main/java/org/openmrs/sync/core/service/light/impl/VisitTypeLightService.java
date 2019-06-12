package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.VisitTypeLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.AbstractLightService;
import org.openmrs.sync.core.service.attribute.AttributeUuid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VisitTypeLightService extends AbstractLightService<VisitTypeLight> {

    public VisitTypeLightService(final OpenMrsRepository<VisitTypeLight> repository) {
        super(repository);
    }

    @Override
    protected VisitTypeLight getFakeEntity(final String uuid,
                                           final List<AttributeUuid> attributeUuids) {
        VisitTypeLight visitType = new VisitTypeLight();
        visitType.setUuid(uuid);
        visitType.setName(DEFAULT_STRING);
        visitType.setCreator(DEFAULT_USER_ID);
        visitType.setDateCreated(DEFAULT_DATE);
        return visitType;
    }
}
