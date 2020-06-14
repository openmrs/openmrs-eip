package org.openmrs.eip.component.service.impl;

import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.model.ErpWorkOrderStateModel;
import org.openmrs.eip.component.service.AbstractEntityService;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.openmrs.eip.component.entity.ErpWorkOrderState;
import org.openmrs.eip.component.repository.SyncEntityRepository;

//@Service
public class ErpWorkOrderStateService extends AbstractEntityService<ErpWorkOrderState, ErpWorkOrderStateModel> {

    public ErpWorkOrderStateService(SyncEntityRepository<ErpWorkOrderState> repository, EntityToModelMapper<ErpWorkOrderState, ErpWorkOrderStateModel> entityToModelMapper, ModelToEntityMapper<ErpWorkOrderStateModel, ErpWorkOrderState> modelToEntityMapper) {
        super(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Override
    public TableToSyncEnum getTableToSync() {
        return null;//TableToSyncEnum.ICRC_ERP_WORK_ORDER_STATE;
    }

}
