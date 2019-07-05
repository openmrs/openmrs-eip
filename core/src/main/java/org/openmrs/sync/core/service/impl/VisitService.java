package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.entity.Visit;
import org.openmrs.sync.core.mapper.EntityToModelMapper;
import org.openmrs.sync.core.mapper.ModelToEntityMapper;
import org.openmrs.sync.core.model.VisitModel;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.AbstractEntityService;
import org.openmrs.sync.core.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class VisitService extends AbstractEntityService<Visit, VisitModel> {

    public VisitService(final SyncEntityRepository<Visit> repository,
                        final EntityToModelMapper<Visit, VisitModel> entityToModelMapper,
                        final ModelToEntityMapper<VisitModel, Visit> modelToEntityMapper) {
        super(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Override
    public TableToSyncEnum getTableToSync() {
        return TableToSyncEnum.VISIT;
    }
}
