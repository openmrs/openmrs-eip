package org.openmrs.sync.component.service.impl;

import org.openmrs.sync.component.entity.BaseEntity;
import org.openmrs.sync.common.model.sync.BaseModel;
import org.openmrs.sync.component.service.MapperService;
import org.openmrs.sync.component.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class MapperServiceImpl implements MapperService {

    @Override
    public Class<? extends BaseModel> getCorrespondingModelClass(final BaseEntity entity) {
        return TableToSyncEnum.getModelClass(entity);
    }

    @Override
    public Class<? extends BaseEntity> getCorrespondingEntityClass(final BaseModel model) {
        return TableToSyncEnum.getEntityClass(model);
    }
}
