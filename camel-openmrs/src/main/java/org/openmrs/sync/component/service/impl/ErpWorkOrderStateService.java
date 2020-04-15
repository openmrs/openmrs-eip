package org.openmrs.sync.component.service.impl;

import org.openmrs.sync.component.entity.ErpWorkOrderState;
import org.openmrs.sync.component.mapper.EntityToModelMapper;
import org.openmrs.sync.component.mapper.ModelToEntityMapper;
import org.openmrs.sync.component.model.ErpWorkOrderStateModel;
import org.openmrs.sync.component.repository.SyncEntityRepository;
import org.openmrs.sync.component.service.AbstractEntityService;
import org.openmrs.sync.component.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class ErpWorkOrderStateService extends AbstractEntityService<ErpWorkOrderState, ErpWorkOrderStateModel> {

    public ErpWorkOrderStateService(SyncEntityRepository<ErpWorkOrderState> repository, EntityToModelMapper<ErpWorkOrderState, ErpWorkOrderStateModel> entityToModelMapper, ModelToEntityMapper<ErpWorkOrderStateModel, ErpWorkOrderState> modelToEntityMapper) {
        super(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Override
    public TableToSyncEnum getTableToSync() {
        return TableToSyncEnum.ERP_WORK_ORDER_STATE;
    }

}
