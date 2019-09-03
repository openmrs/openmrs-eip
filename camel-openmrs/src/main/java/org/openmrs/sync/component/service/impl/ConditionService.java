package org.openmrs.sync.component.service.impl;

import org.openmrs.sync.component.entity.Condition;
import org.openmrs.sync.component.mapper.EntityToModelMapper;
import org.openmrs.sync.component.mapper.ModelToEntityMapper;
import org.openmrs.sync.component.model.ConditionModel;
import org.openmrs.sync.component.repository.SyncEntityRepository;
import org.openmrs.sync.component.service.AbstractEntityService;
import org.openmrs.sync.component.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class ConditionService extends AbstractEntityService<Condition, ConditionModel> {

    public ConditionService(final SyncEntityRepository<Condition> repository,
                            final EntityToModelMapper<Condition, ConditionModel> entityToModelMapper,
                            final ModelToEntityMapper<ConditionModel, Condition> modelToEntityMapper) {
        super(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Override
    public TableToSyncEnum getTableToSync() {
        return TableToSyncEnum.CONDITION;
    }
}
