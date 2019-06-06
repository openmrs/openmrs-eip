package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.entity.light.VisitTypeLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.AbstractSimpleService;
import org.springframework.stereotype.Service;

@Service
public class VisitTypeService extends AbstractSimpleService<VisitTypeLight> {

    public VisitTypeService(final OpenMrsRepository<VisitTypeLight> repository) {
        super(repository);
    }

    @Override
    protected VisitTypeLight getFakeEntity(final String uuid) {
        VisitTypeLight visitType = new VisitTypeLight();
        visitType.setUuid(uuid);
        visitType.setName(DEFAULT_STRING);
        visitType.setCreator(DEFAULT_USER_ID);
        visitType.setDateCreated(DEFAULT_DATE);
        return visitType;
    }
}
