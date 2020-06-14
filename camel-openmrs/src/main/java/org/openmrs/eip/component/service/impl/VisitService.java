package org.openmrs.eip.component.service.impl;

import org.openmrs.eip.component.model.VisitModel;
import org.openmrs.eip.component.entity.Visit;
import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.repository.SyncEntityRepository;
import org.openmrs.eip.component.service.AbstractEntityService;
import org.openmrs.eip.component.service.TableToSyncEnum;
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
