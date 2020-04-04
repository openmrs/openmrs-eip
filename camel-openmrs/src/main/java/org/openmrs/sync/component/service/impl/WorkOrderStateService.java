package org.openmrs.sync.component.service.impl;

import org.openmrs.sync.component.entity.WorkOrderState;
import org.openmrs.sync.component.mapper.EntityToModelMapper;
import org.openmrs.sync.component.mapper.ModelToEntityMapper;
import org.openmrs.sync.component.model.WorkOrderStateModel;
import org.openmrs.sync.component.repository.SyncEntityRepository;
import org.openmrs.sync.component.service.AbstractEntityService;
import org.openmrs.sync.component.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class WorkOrderStateService extends AbstractEntityService<WorkOrderState, WorkOrderStateModel> {

    public WorkOrderStateService(SyncEntityRepository<WorkOrderState> repository, EntityToModelMapper<WorkOrderState, WorkOrderStateModel> entityToModelMapper, ModelToEntityMapper<WorkOrderStateModel, WorkOrderState> modelToEntityMapper) {
        super(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Override
    public TableToSyncEnum getTableToSync() {
        return TableToSyncEnum.WORK_ORDER_STATE;
    }

}
