package org.openmrs.sync.component.service.impl;

import org.openmrs.sync.component.entity.Visit;
import org.openmrs.sync.component.mapper.EntityToModelMapper;
import org.openmrs.sync.component.mapper.ModelToEntityMapper;
import org.openmrs.sync.component.model.VisitModel;
import org.openmrs.sync.component.repository.SyncEntityRepository;
import org.openmrs.sync.component.service.AbstractEntityService;
import org.openmrs.sync.component.service.TableToSyncEnum;
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
