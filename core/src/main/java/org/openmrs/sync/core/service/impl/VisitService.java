package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.entity.Visit;
import org.openmrs.sync.core.mapper.EntityMapper;
import org.openmrs.sync.core.model.VisitModel;
import org.openmrs.sync.core.repository.AuditableRepository;
import org.openmrs.sync.core.service.AbstractEntityService;
import org.openmrs.sync.core.service.EntityNameEnum;
import org.springframework.stereotype.Service;

@Service
public class VisitService extends AbstractEntityService<Visit, VisitModel> {

    public VisitService(final AuditableRepository<Visit> repository,
                        final EntityMapper<Visit, VisitModel> mapper) {
        super(repository, mapper);
    }

    @Override
    public EntityNameEnum getEntityName() {
        return EntityNameEnum.VISIT;
    }
}
